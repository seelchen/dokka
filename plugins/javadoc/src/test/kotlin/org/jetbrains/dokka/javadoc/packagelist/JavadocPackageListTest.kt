/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.dokka.javadoc.packagelist

import org.jetbrains.dokka.javadoc.AbstractJavadocTemplateMapTest
import utils.TestOutputWriterPlugin
import kotlin.test.Test
import kotlin.test.assertEquals

internal class JavadocPackageListTest : AbstractJavadocTemplateMapTest() {
    @Test
    fun `package list should be generated for a flat structure`(){
        val writerPlugin = TestOutputWriterPlugin()
        dualTestTemplateMapInline(
            java = """
                /src/package0/ClassA.java
                package package0
                public class ClassA {
                
                }
                
                /src/package1/ClassB.java
                package package1
                public class ClassB {
                }
            """,
            pluginsOverride = listOf(writerPlugin)
        ){
            val contents = writerPlugin.writer.contents
            val expected = """
                ${'$'}dokka.format:javadoc-v1
                ${'$'}dokka.linkExtension:html
                
                package0
                package1
                """.trimIndent()
            assertEquals(expected, contents["element-list"]?.trimIndent())
            assertEquals(expected, contents["package-list"]?.trimIndent())
        }
    }

    @Test
    fun `package list should be generated for nested structure`(){
        val writerPlugin = TestOutputWriterPlugin()
        dualTestTemplateMapInline(
            java = """
                /src/package0/ClassA.java
                package package0
                public class ClassA {
                
                }
                
                /src/package0/package0Inner/ClassB.java
                package package0.package0Inner
                public class ClassB {
                }
                
                /src/package1/package1Inner/package1InnerInner/ClassC.java
                package package1.package1Inner.package1InnerInner
                public class ClassC {
                }
            """,
            pluginsOverride = listOf(writerPlugin)
        ){
            val contents = writerPlugin.writer.contents
            val expected = """
                ${'$'}dokka.format:javadoc-v1
                ${'$'}dokka.linkExtension:html
                
                package0
                package0.package0Inner
                package1.package1Inner.package1InnerInner
                """.trimIndent()
            assertEquals(expected, contents["element-list"]?.trimIndent())
            assertEquals(expected, contents["package-list"]?.trimIndent())
        }
    }
}
