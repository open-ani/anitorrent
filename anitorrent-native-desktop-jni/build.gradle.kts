/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    java
    idea
    id(libs.plugins.vanniktech.mavenPublish.get().pluginId)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(8)
}

sourceSets.main {
    java.srcDirs(layout.buildDirectory.dir("generated-sources/swig"))
}

val copyGeneratedSwigJava = tasks.register("copyGeneratedSwigJava", Copy::class) {
    dependsOn(projects.anitorrentNative.dependencyProject.tasks.named("generateSwigImpl"))
    from(projects.anitorrentNative.dependencyProject.projectDir.resolve("gen/java"))
    into(layout.buildDirectory.dir("generated-sources/swig"))
}

tasks.compileJava {
    dependsOn(copyGeneratedSwigJava)
}

mavenPublishing {
    configure(JavaLibrary(JavadocJar.Empty(), true))
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        name = "Anitorrent Native"
        description = "Anitorrent Native"
        url = "https://github.com/open-ani/anitorrent"

        licenses {
            license {
                name = "GNU General Public License, Version 3"
                url = "https://github.com/open-ani/anitorrent/blob/main/LICENSE"
                distribution = "https://www.gnu.org/licenses/gpl-3.0.txt"
            }
        }

        developers {
            developer {
                id = "openani"
                name = "OpenAni and contributors"
                email = "support@openani.org"
            }
        }

        scm {
            connection = "scm:git:https://github.com/open-ani/anitorrent.git"
            developerConnection = "scm:git:git@github.com:open-ani/anitorrent.git"
            url = "https://github.com/open-ani/anitorrent"
        }
    }
}

tasks.getByName("sourcesJar") {
    dependsOn(copyGeneratedSwigJava)
}
