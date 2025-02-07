/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package utils

import org.jetbrains.dokka.DokkaConfigurationImpl
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.plugability.DokkaPlugin

abstract class AbstractModelTest(val path: String? = null, val pkg: String) : ModelDSL(), AssertDSL {

    fun inlineModelTest(
        query: String,
        platform: String = "jvm",
        prependPackage: Boolean = true,
        cleanupOutput: Boolean = true,
        pluginsOverrides: List<DokkaPlugin> = emptyList(),
        configuration: DokkaConfigurationImpl? = null,
        block: DModule.() -> Unit
    ) {
        val testConfiguration = configuration ?: dokkaConfiguration {
            suppressObviousFunctions = false
            sourceSets {
                sourceSet {
                    sourceRoots = listOf("src/")
                    analysisPlatform = platform
                    classpath += jvmStdlibPath!!
                }
            }
        }
        val prepend = path.let { p -> p?.let { "|$it\n" } ?: "" } + if (prependPackage) "|package $pkg" else ""

        testInline(
            query = ("$prepend\n$query").trim().trimIndent(),
            configuration = testConfiguration,
            cleanupOutput = cleanupOutput,
            pluginOverrides = pluginsOverrides
        ) {
            documentablesTransformationStage = block
        }
    }
}
