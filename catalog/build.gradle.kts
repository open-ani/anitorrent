/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `version-catalog`
    id(libs.plugins.vanniktech.mavenPublish.get().pluginId)
}

description = "Anitorrent Version Catalogs"

catalog {
    versionCatalog {
        version("anitorrent", project.version.toString())
        library("anitorrent-native", project.group.toString(), "anitorrent-native").versionRef("anitorrent")
        library(
            "anitorrent-native-android",
            project.group.toString(),
            "anitorrent-native-android",
        ).versionRef("anitorrent")
        library(
            "anitorrent-native-desktop",
            project.group.toString(),
            "anitorrent-native-desktop",
        ).versionRef("anitorrent")
        library(
            "anitorrent-native-desktop-jni",
            project.group.toString(),
            "anitorrent-native-desktop-jni",
        ).versionRef("anitorrent")
    }
}

mavenPublishing {
    configure(com.vanniktech.maven.publish.VersionCatalog())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    configurePom(project)
}
