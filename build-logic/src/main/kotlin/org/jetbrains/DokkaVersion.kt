/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate

@Suppress("LocalVariableName") // property name with underscore as taken from gradle.properties
fun Project.configureDokkaVersion(): String {
    val dokka_version: String? by this.extra
    return checkNotNull(dokka_version)
}

val Project.dokkaVersion: String
    get() = configureDokkaVersion()

val Project.dokkaVersionType: DokkaVersionType?
    get() = DokkaVersionType.values().find {
        it.suffix.matches(dokkaVersion.substringAfter("-", ""))
    }
