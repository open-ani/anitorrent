#!/usr/bin/env kotlin

/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

// 也可以在 IDE 里右键 Run

@file:CompilerOptions("-Xmulti-dollar-interpolation", "-Xdont-warn-on-error-suppression")
@file:Suppress("UNSUPPORTED_FEATURE", "UNSUPPORTED")

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.0.1")
@file:Repository("https://bindings.krzeminski.it")

// Build
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("org.jetbrains:annotations:23.0.0")
@file:DependsOn("actions:github-script:v7")
@file:DependsOn("gradle:actions__setup-gradle:v3")
@file:DependsOn("nick-fields:retry:v2")
@file:DependsOn("timheuer:base64-to-file:v1.1")
@file:DependsOn("actions:upload-artifact:v4")
@file:DependsOn("actions:download-artifact:v4")

// Release
@file:DependsOn("dawidd6:action-get-tag:v1")
@file:DependsOn("bhowell2:github-substring-action:v1.0.0")
@file:DependsOn("softprops:action-gh-release:v1")
@file:DependsOn("snow-actions:qrcode:v1.0.0")


import Secrets.GITHUB_REPOSITORY
import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.DownloadArtifact
import io.github.typesafegithub.workflows.actions.actions.GithubScript
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.bhowell2.GithubSubstringAction_Untyped
import io.github.typesafegithub.workflows.actions.dawidd6.ActionGetTag_Untyped
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.actions.nickfields.Retry_Untyped
import io.github.typesafegithub.workflows.actions.softprops.ActionGhRelease
import io.github.typesafegithub.workflows.domain.ActionStep
import io.github.typesafegithub.workflows.domain.CommandStep
import io.github.typesafegithub.workflows.domain.Job
import io.github.typesafegithub.workflows.domain.JobOutputs
import io.github.typesafegithub.workflows.domain.Mode
import io.github.typesafegithub.workflows.domain.Permission
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.JobBuilder
import io.github.typesafegithub.workflows.dsl.expressions.contexts.GitHubContext
import io.github.typesafegithub.workflows.dsl.expressions.contexts.SecretsContext
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig
import org.intellij.lang.annotations.Language

check(KotlinVersion.CURRENT.isAtLeast(2, 0, 0)) {
    "This script requires Kotlin 2.0.0 or later"
}

enum class OS {
    WINDOWS,
    UBUNTU,
    MACOS;

    override fun toString(): String = name.lowercase()
}

enum class Arch {
    X64,
    AARCH64;

    override fun toString(): String = name.lowercase()
}

//enum class AndroidArch(
//    val id: String,
//) {
//    ARM64_V8A("arm64-v8a"),
//    X86_64("x86_64"),
//    ARMEABI_V7A("armeabi-v7a"),
//    UNIVERSAL("universal"),
//    ;
//}

object AndroidArch {
    const val ARM64_V8A = "arm64-v8a"
    const val X86_64 = "x86_64"
    const val ARMEABI_V7A = "armeabi-v7a"
    const val UNIVERSAL = "universal"

    val entriesWithoutUniversal = listOf(ARM64_V8A, X86_64, ARMEABI_V7A)
    val entriesWithUniversal = entriesWithoutUniversal + UNIVERSAL
}

// Build 和 Release 共享这个
// Configuration for a Runner
class MatrixInstance(
    // 定义属性为 val, 就会生成到 yml 的 `matrix` 里.

    /**
     * 用于 matrix 的 id
     */
    val runner: Runner,
    /**
     * 显示的名字, 不能变更, 否则会导致 PR Rules 失效
     */
    val name: String = runner.name,
    /**
     * GitHub Actions 的规范名称, e.g. `ubuntu-20.04`, `windows-2019`.
     */
    val runsOn: Set<String> = runner.labels,

    /**
     * 只在脚本内部判断 OS 使用, 不影响 github 调度机器
     * @see OS
     */
    val os: OS = runner.os,
    /**
     * 只在脚本内部判断 OS 使用, 不影响 github 调度机器
     * @see Arch
     */
    val arch: Arch = runner.arch,

    /**
     * `false` = GitHub Actions 的免费机器
     */
    val selfHosted: Boolean = runner is Runner.SelfHosted,
    /**
     * 有一台机器是 true 就行
     */
    val uploadApk: Boolean,
    val buildAnitorrent: Boolean,
    val buildAnitorrentSeparately: Boolean,
    /**
     * Compose for Desktop 的 resource 标识符, e.g. `windows-x64`
     */
    val composeResourceTriple: String,
    val runTests: Boolean = true,
    /**
     * 每种机器必须至少有一个是 true, 否则 release 时传不全
     */
    val uploadDesktopInstallers: Boolean = true,
    /**
     * 追加到所有 Gradle 命令的参数. 无需 quote
     */
    val extraGradleArgs: List<String> = emptyList(),
    /**
     * Self hosted 机器已经配好了环境, 无需安装
     */
    val installNativeDeps: Boolean = !selfHosted,
    val buildIosFramework: Boolean = false,
    val buildAllAndroidAbis: Boolean = true,

    // Gradle command line args
    gradleHeap: String = "4g",
    kotlinCompilerHeap: String = "4g",
    /**
     * 只能在内存比较大的时候用.
     */
    gradleParallel: Boolean = selfHosted,
) {
    @Suppress("unused")
    val gradleArgs = buildList {

        /**
         * Windows 上必须 quote, Unix 上 quote 或不 quote 都行. 所以我们统一 quote.
         */
        fun quote(s: String): String {
            if (s.startsWith("\"")) {
                return s  // already quoted
            }
            return "\"$s\""
        }

        add(quote("--scan"))
        add(quote("--no-configuration-cache"))
        add(quote("-Porg.gradle.daemon.idletimeout=60000"))
        add(quote("-Pkotlin.native.ignoreDisabledTargets=true"))
        add(quote("-Dfile.encoding=UTF-8"))

        if (buildAnitorrent) {
            add(quote("-Dani.enable.anitorrent=true"))
            add(quote("-DCMAKE_BUILD_TYPE=Release"))
        }

        if (os == OS.WINDOWS) {
            add(quote("-DCMAKE_TOOLCHAIN_FILE=C:/vcpkg/scripts/buildsystems/vcpkg.cmake"))
            add(quote("-DBoost_INCLUDE_DIR=C:/vcpkg/installed/x64-windows/include"))
        }

        add(quote("-Dorg.gradle.jvmargs=-Xmx${gradleHeap}"))
        add(quote("-Dkotlin.daemon.jvm.options=-Xmx${kotlinCompilerHeap}"))

        if (gradleParallel) {
            add(quote("--parallel"))
        }

        extraGradleArgs.forEach {
            add(quote(it))
        }
    }.joinToString(" ")

    init {
        if (buildAllAndroidAbis) {
            require(!gradleArgs.contains(ANI_ANDROID_ABIS)) { "You must not set `-P${ANI_ANDROID_ABIS}` when you want to build all Android ABIs" }
        } else {
            require(gradleArgs.contains(ANI_ANDROID_ABIS)) { "You must set `-P${ANI_ANDROID_ABIS}` when you don't want to build all Android ABIs" }
        }
    }
}

@Suppress("PropertyName")
val ANI_ANDROID_ABIS = "ani.android.abis"

sealed class Runner(
    val id: String,
    val name: String,
    val os: OS,
    val arch: Arch,
    // GitHub Actions labels, e.g. `windows-2019`, `macos-13`, `self-hosted`, `Windows`, `X64`
    val labels: Set<String>,
) {
    // Intermediate sealed classes
    sealed class GithubHosted(
        id: String,
        displayName: String,
        os: OS,
        arch: Arch,
        labels: Set<String>
    ) : Runner(id, displayName, os, arch, labels)

    sealed class SelfHosted(
        id: String,
        displayName: String,
        os: OS,
        arch: Arch,
        labels: Set<String>
    ) : Runner(id, displayName, os, arch, labels)

    // Objects under GithubHosted
    object GithubWindowsServer2019 : GithubHosted(
        id = "github-windows-2019",
        displayName = "Windows Server 2019 x86_64 (GitHub)",
        os = OS.WINDOWS,
        arch = Arch.X64,
        labels = setOf("windows-2019"),
    )

    object GithubWindowsServer2022 : GithubHosted(
        id = "github-windows-2022",
        displayName = "Windows Server 2022 x86_64 (GitHub)",
        os = OS.WINDOWS,
        arch = Arch.X64,
        labels = setOf("windows-2022"),
    )

    object GithubMacOS13 : GithubHosted(
        id = "github-macos-13",
        displayName = "macOS 13 x86_64 (GitHub)",
        os = OS.MACOS,
        arch = Arch.X64,
        labels = setOf("macos-13"),
    )

    object GithubMacOS14 : GithubHosted(
        id = "github-macos-14",
        displayName = "macOS 14 AArch64 (GitHub)",
        os = OS.MACOS,
        arch = Arch.AARCH64,
        labels = setOf("macos-14"),
    )

    object GithubMacOS15 : GithubHosted(
        id = "github-macos-15",
        displayName = "macOS 15 AArch64 (GitHub)",
        os = OS.MACOS,
        arch = Arch.AARCH64,
        labels = setOf("macos-15"),
    )

    object GithubUbuntu2004 : GithubHosted(
        id = "github-ubuntu-2004",
        displayName = "Ubuntu 20.04 x86_64 (GitHub)",
        os = OS.UBUNTU,
        arch = Arch.X64,
        labels = setOf("ubuntu-20.04"),
    )

    object GithubUbuntu2404 : GithubHosted(
        id = "github-ubuntu-2404",
        displayName = "Ubuntu 24.04 x86_64 (GitHub)",
        os = OS.UBUNTU,
        arch = Arch.X64,
        labels = setOf("ubuntu-24.04"),
    )

    // Objects under SelfHosted
    object SelfHostedWindows10 : SelfHosted(
        id = "self-hosted-windows-10",
        displayName = "Windows 10 x86_64 (Self-Hosted)",
        os = OS.WINDOWS,
        arch = Arch.X64,
        labels = setOf("self-hosted", "Windows", "X64"),
    )

    object SelfHostedMacOS15 : SelfHosted(
        id = "self-hosted-macos-15",
        displayName = "macOS 15 AArch64 (Self-Hosted)",
        os = OS.MACOS,
        arch = Arch.AARCH64,
        labels = setOf("self-hosted", "macOS", "ARM64"),
    )

//    companion object {
//        val entries: List<Runner> = listOf(
//            GithubWindowsServer2019,
//            GithubWindowsServer2022,
//            GithubMacOS13,
//            GithubMacOS14,
//            GithubUbuntu2004,
//            SelfHostedWindows10,
//            SelfHostedMacOS15,
//        )
//    }

    override fun toString(): String = id
}

val Runner.isSelfHosted: Boolean
    get() = this is Runner.SelfHosted

// Machines for Build and Release
val buildMatrixInstances = listOf(
    MatrixInstance(
        runner = Runner.GithubWindowsServer2019,
        name = "Windows Server 2019 x86_64",
        uploadApk = false,
        buildAnitorrent = true,
        buildAnitorrentSeparately = false, // windows 单线程构建 anitorrent, 要一起跑节约时间
        composeResourceTriple = "windows-x64",
        gradleHeap = "4g",
        kotlinCompilerHeap = "4g",
        gradleParallel = true,
        uploadDesktopInstallers = true,
        extraGradleArgs = listOf(
            "-P$ANI_ANDROID_ABIS=x86_64",
        ),
        buildAllAndroidAbis = false,
    ),
    MatrixInstance(
        runner = Runner.GithubUbuntu2404,
        name = "Ubuntu 24.04 LTS x86_64",
        uploadApk = false,
        buildAnitorrent = true,
        buildAnitorrentSeparately = true,
        composeResourceTriple = "linux-x64",
        uploadDesktopInstallers = true,
        extraGradleArgs = listOf(),
        gradleHeap = "4g",
        kotlinCompilerHeap = "4g",
        buildAllAndroidAbis = true,
    ),
    MatrixInstance(
        runner = Runner.GithubMacOS13,
        uploadApk = true, // all ABIs
        buildAnitorrent = true,
        buildAnitorrentSeparately = true,
        composeResourceTriple = "macos-x64",
        buildIosFramework = false,
        gradleHeap = "4g",
        kotlinCompilerHeap = "4g",
        uploadDesktopInstallers = true,
        extraGradleArgs = listOf(),
        buildAllAndroidAbis = true,
    ),
    MatrixInstance(
        runner = Runner.SelfHostedMacOS15,
        uploadApk = false, // upload arm64-v8a once finished
        buildAnitorrent = true,
        buildAnitorrentSeparately = true,
        composeResourceTriple = "macos-arm64",
        uploadDesktopInstallers = true,
        extraGradleArgs = listOf(
            "-P$ANI_ANDROID_ABIS=arm64-v8a", // speed up testing
        ),
        buildIosFramework = false,
        gradleHeap = "6g",
        kotlinCompilerHeap = "4g",
        gradleParallel = true,
        buildAllAndroidAbis = false,
    ),
)

class BuildJobOutputs : JobOutputs()

fun getBuildJobBody(matrix: MatrixInstance): JobBuilder<BuildJobOutputs>.() -> Unit = {
    uses(action = Checkout(submodules_Untyped = "recursive"))

    with(WithMatrix(matrix)) {
        freeSpace()
        installJdk()
        installNativeDeps()
        chmod777()
        setupGradle()

        gradleCheck()
        runGradle(
            name = "Build anitorrent",
            tasks = ["buildAnitorrent", "copyNativeJarForCurrentPlatform"],
        )
        uploadAnitorrent()

        cleanupTempFiles()
    }
}

object ArtifactNames {
    fun anitorrentNativeJar(os: OS, arch: Arch) = "anitorrent-${os}-${arch}"
}

workflow(
    name = "Build",
    on = listOf(
        // Including: 
        // - pushing directly to main
        // - pushing to a branch that has an associated PR
        Push(pathsIgnore = listOf("**/*.md")),
        PullRequest(pathsIgnore = listOf("**/*macosDmg.md")),
    ),
    sourceFile = __FILE__,
    targetFileName = "build.yml",
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled,
) {
    // Expands job matrix at compile-time so that we set job-level `if` condition. 
    buildMatrixInstances.map { matrix ->
        matrix to job(
            id = "build_${matrix.runner.id}",
            name = """Build (${matrix.name})""",
            runsOn = RunnerType.Labelled(matrix.runsOn),
            permissions = mapOf(
                Permission.Actions to Mode.Write, // Upload artifacts
            ),
            `if` = if (matrix.selfHosted) {
                // For self-hosted runners, only run if it's our main repository (not a fork).
                // For security concerns, all external contributors will need approval to run the workflow.
                expr { github.isAnimekoRepository }
            } else {
                null // always
            },
            outputs = BuildJobOutputs(),
            block = getBuildJobBody(matrix),
        )
    }
}

operator fun List<Pair<MatrixInstance, Job<BuildJobOutputs>>>.get(runner: Runner): Job<BuildJobOutputs> {
    return first { it.first.runner == runner }.second
}

operator fun List<MatrixInstance>.get(runner: Runner): MatrixInstance {
    return first { it.runner == runner }
}

workflow(
    name = "Release",
    permissions = mapOf(
        Permission.Actions to Mode.Write,
        Permission.Contents to Mode.Write, // Releases
    ),
    on = listOf(
        // Only commiter with write-access can trigger this
        Push(tags = listOf("v*")),
    ),
    sourceFile = __FILE__,
    targetFileName = "release.yml",
    consistencyCheckJobConfig = ConsistencyCheckJobConfig.Disabled,
) {
    val createRelease = job(
        id = "create-release",
        name = "Create Release",
        runsOn = RunnerType.UbuntuLatest,
        outputs = object : JobOutputs() {
            var uploadUrl by output()
            var id by output()
        },
    ) {
        uses(action = Checkout()) // No need to be recursive

        val gitTag = getGitTag()

        val releaseNotes = run(
            name = "Generate Release Notes",
            command = shell(
                $$"""
                  # Specify the file path
                  FILE_PATH="ci-helper/release-template.md"
        
                  # Read the file content
                  file_content=$(cat "$FILE_PATH")
        
                  modified_content="$file_content"
                  # Replace 'string_to_find' with 'string_to_replace_with' in the content
                  modified_content="${modified_content//\$GIT_TAG/$${expr { gitTag.tagExpr }}}"
                  modified_content="${modified_content//\$TAG_VERSION/$${expr { gitTag.tagVersionExpr }}}"
        
                  # Output the result as a step output
                  echo "result<<EOF" >> $GITHUB_OUTPUT
                  echo "$modified_content" >> $GITHUB_OUTPUT
                  echo "EOF" >> $GITHUB_OUTPUT
            """.trimIndent(),
            ),
        )

        val createRelease = uses(
            name = "Create Release",
            action = ActionGhRelease(
                tagName = expr { gitTag.tagExpr },
                name = expr { gitTag.tagVersionExpr },
                body = expr { releaseNotes.outputs["result"] },
                draft = true,
                prerelease_Untyped = expr { contains(gitTag.tagExpr, "'-'") },
            ),
            env = mapOf("GITHUB_TOKEN" to expr { secrets.GITHUB_TOKEN }),
        )

        jobOutputs.uploadUrl = createRelease.outputs.uploadUrl
        jobOutputs.id = createRelease.outputs.id
    }

    fun addJob(
        matrix: MatrixInstance,
        needs: List<Job<*>> = emptyList(),
        additionalSteps: JobBuilder<*>.(matrix: MatrixInstance) -> Unit = {},
    ) = job(
        id = "release_${matrix.runner.id}",
        name = matrix.name,
        needs = listOf(createRelease) + needs,
        runsOn = RunnerType.Labelled(matrix.runsOn),
        `if` = if (matrix.selfHosted) expr { github.isAnimekoRepository } else null, // Don't run on forks
        outputs = object : JobOutputs() {},
        block = { ->
            with(WithMatrix(matrix)) {
                uses(action = Checkout(submodules_Untyped = "recursive"))

                val gitTag = getGitTag()

                freeSpace()
                installJdk()
                installNativeDeps()
                chmod777()
                setupGradle()

                runGradle(
                    name = "Update Release Version Name",
                    tasks = ["updateReleaseVersionNameFromGit"],
                    env = mapOf(
                        "GITHUB_TOKEN" to expr { secrets.GITHUB_TOKEN },
                        "GITHUB_REPOSITORY" to expr { secrets.GITHUB_REPOSITORY },
                        "CI_RELEASE_ID" to expr { createRelease.outputs.id },
                        "CI_TAG" to expr { gitTag.tagExpr },
                    ),
                )


                runGradle(
                    name = "Build anitorrent",
                    tasks = ["buildAnitorrent", "copyNativeJarForCurrentPlatform"],
                )
                // no check
                uploadAnitorrent()

                additionalSteps(matrix)

                cleanupTempFiles()
            }
        },
    )

    val win = addJob(buildMatrixInstances[Runner.GithubWindowsServer2019])
    val macAarch64 = addJob(buildMatrixInstances[Runner.SelfHostedMacOS15])
    val ubuntu = addJob(buildMatrixInstances[Runner.GithubUbuntu2404])
    addJob(buildMatrixInstances[Runner.GithubMacOS13], needs = listOf(win, macAarch64, ubuntu)) { matrix ->
        with(WithMatrix(matrix)) {
            listOf(
                OS.WINDOWS to Arch.X64,
                OS.MACOS to Arch.AARCH64,
                OS.UBUNTU to Arch.X64,
            ).forEach { (os, arch) ->
                uses(
                    action = DownloadArtifact(
                        name = ArtifactNames.anitorrentNativeJar(os, arch),
                        path = "anitorrent-native/build/native-jars",
                    ),
                )
            }

            run(command = "ls -l anitorrent-native/build/native-jars")
            runGradle(
                tasks = ["publish"],
                env = mapOf(
                    "ORG_GRADLE_PROJECT_mavenCentralUsername" to expr { secrets["ORG_GRADLE_PROJECT_mavenCentralUsername"]!! },
                    "ORG_GRADLE_PROJECT_mavenCentralPassword" to expr { secrets["ORG_GRADLE_PROJECT_mavenCentralPassword"]!! },
                    "ORG_GRADLE_PROJECT_signingInMemoryKey" to expr { secrets["ORG_GRADLE_PROJECT_signingInMemoryKey"]!! },
                    "ORG_GRADLE_PROJECT_signingInMemoryKeyId" to expr { secrets["ORG_GRADLE_PROJECT_signingInMemoryKeyId"]!! },
                    "ORG_GRADLE_PROJECT_signingInMemoryKeyPassword" to expr { secrets["ORG_GRADLE_PROJECT_signingInMemoryKeyPassword"]!! },
                ),
            )
        }
    }
}

data class GitTag(
    /**
     * The full git tag, e.g. `v1.0.0`
     */
    val tagExpr: String,
    /**
     * The tag version, e.g. `1.0.0`
     */
    val tagVersionExpr: String,
)

fun JobBuilder<*>.getGitTag(): GitTag {
    val tag = uses(
        name = "Get Tag",
        action = ActionGetTag_Untyped(),
    )

    val tagVersion = uses(
        action = GithubSubstringAction_Untyped(
            value_Untyped = expr { tag.outputs.tag },
            indexOfStr_Untyped = "v",
            defaultReturnValue_Untyped = expr { tag.outputs.tag },
        ),
    )

    return GitTag(
        tagExpr = tag.outputs.tag,
        tagVersionExpr = tagVersion.outputs["substring"],
    )
}

class WithMatrix(
    val matrix: MatrixInstance
) {
    fun JobBuilder<*>.runGradle(
        name: String? = null,
        `if`: String? = null,
        @Language("shell", prefix = "./gradlew ") vararg tasks: String,
        env: Map<String, String> = emptyMap(),
    ): CommandStep = run(
        name = name,
        `if` = `if`,
        command = shell(
            buildString {
                append("./gradlew ")
                tasks.joinTo(this, " ")
                append(' ')
                append(matrix.gradleArgs)
            },
        ),
        env = env,
    )

    /**
     * GitHub Actions 上给的硬盘比较少, 我们删掉一些不必要的文件来腾出空间.
     */
    fun JobBuilder<*>.freeSpace() {
        if (matrix.isMacOS && !matrix.selfHosted) {
            run(
                name = "Free space for macOS",
                command = shell($$"""chmod +x ./ci-helper/free-space-macos.sh && ./ci-helper/free-space-macos.sh"""),
                continueOnError = true,
            )
        }
    }

    fun JobBuilder<*>.installJdk() {
        uses(
            name = "Setup JDK 21",
            action = SetupJava(
                distribution = SetupJava.Distribution.Temurin,
                javaVersion = "21",
            ),
            env = mapOf("GITHUB_TOKEN" to expr { secrets.GITHUB_TOKEN }),
        )
        run(
            command = shell($$"""echo "jvm.toolchain.version=21" >> local.properties"""),
        )
    }

    fun JobBuilder<*>.installNativeDeps() {
        // Windows
        if (matrix.isWindows and matrix.installNativeDeps) {
            uses(
                name = "Setup vcpkg cache",
                action = GithubScript(
                    script = """
                core.exportVariable('ACTIONS_CACHE_URL', process.env.ACTIONS_CACHE_URL || '');
                core.exportVariable('ACTIONS_RUNTIME_TOKEN', process.env.ACTIONS_RUNTIME_TOKEN || '');
            """.trimIndent(),
                ),
            )
            run(
                name = "Install Native Dependencies for Windows",
                command = "./ci-helper/install-deps-windows.cmd",
                env = mapOf("VCPKG_BINARY_SOURCES" to "clear;x-gha,readwrite"),
            )
        }

        if (matrix.isMacOS and matrix.installNativeDeps) {
            // MacOS
            run(
                name = "Install Native Dependencies for MacOS",
                command = shell($$"""chmod +x ./ci-helper/install-deps-macos-ci.sh && ./ci-helper/install-deps-macos-ci.sh"""),
            )
        }

        if (matrix.isUbuntu and matrix.installNativeDeps) {
            run(
                name = "Install Native Dependencies for Ubuntu",
                command = shell($$"""chmod +x ./ci-helper/install-deps-ubuntu.sh && ./ci-helper/install-deps-ubuntu.sh"""),
            )
        }
    }

    fun JobBuilder<*>.chmod777() {
        if (matrix.isUnix) {
            run(
                command = "chmod -R 777 .",
            )
        }
    }

    fun JobBuilder<*>.setupGradle() {
        uses(
            name = "Setup Gradle",
            action = ActionsSetupGradle(
                cacheDisabled = true,
            ),
        )
        uses(
            name = "Clean and download dependencies",
            action = Retry_Untyped(
                maxAttempts_Untyped = "3",
                timeoutMinutes_Untyped = "60",
                command_Untyped = """./gradlew """ + matrix.gradleArgs,
            ),
        )
    }

    fun JobBuilder<*>.gradleCheck() {
        if (matrix.runTests) {
            uses(
                name = "Check",
                action = Retry_Untyped(
                    maxAttempts_Untyped = "2",
                    timeoutMinutes_Untyped = "60",
                    command_Untyped = "./gradlew check " + matrix.gradleArgs,
                ),
            )
        }
    }

    fun JobBuilder<*>.uploadAnitorrent(): ActionStep<UploadArtifact.Outputs> {
        return uses(
            name = "Upload Anitorrent",
            action = UploadArtifact(
                name = ArtifactNames.anitorrentNativeJar(matrix.os, matrix.arch),
                path_Untyped = "anitorrent-native/build/native-jars/anitorrent-native-*.jar",
                overwrite = true,
                ifNoFilesFound = UploadArtifact.BehaviorIfNoFilesFound.Error,
            ),
        )
    }

    fun JobBuilder<*>.cleanupTempFiles() {
        if (matrix.selfHosted and matrix.isMacOSAArch64) {
            run(
                name = "Cleanup temp files",
                command = shell("""chmod +x ./ci-helper/cleanup-temp-files-macos.sh && ./ci-helper/cleanup-temp-files-macos.sh"""),
                continueOnError = true,
            )
        }
    }
}

/// ENV

object Secrets {
    val SecretsContext.GITHUB_REPOSITORY by SecretsContext.propertyToExprPath
}


/// EXTENSIONS

val GitHubContext.isAnimekoRepository
    get() = """$repository == 'open-ani/anitorrent'"""

val GitHubContext.isPullRequest
    get() = """$event_name == 'pull_request'"""

val MatrixInstance.isX64 get() = arch == Arch.X64
val MatrixInstance.isAArch64 get() = arch == Arch.AARCH64

val MatrixInstance.isMacOS get() = os == OS.MACOS
val MatrixInstance.isWindows get() = os == OS.WINDOWS
val MatrixInstance.isUbuntu get() = os == OS.UBUNTU
val MatrixInstance.isUnix get() = (os == OS.UBUNTU) or (os == (OS.MACOS))

val MatrixInstance.isMacOSAArch64 get() = (os == OS.MACOS) and (arch == Arch.AARCH64)
val MatrixInstance.isMacOSX64 get() = (os == OS.MACOS) and (arch == Arch.X64)

// only for highlighting (though this does not work in KT 2.1.0)
fun shell(@Language("shell") command: String) = command

infix fun String.and(other: String) = "($this) && ($other)"
infix fun String.or(other: String) = "($this) || ($other)"

// 由于 infix 优先级问题, 这里要求使用传统调用方式.
fun String.eq(other: OS) = this.eq(other.toString())
fun String.eq(other: String) = "($this == '$other')"
fun String.eq(other: Boolean) = "($this == $other)"
fun String.neq(other: String) = "($this != '$other')"
fun String.neq(other: Boolean) = "($this != $other)"

operator fun String.not() = "!($this)"
