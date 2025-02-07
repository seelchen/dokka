/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package transformers

import org.jetbrains.dokka.base.transformers.documentables.isException
import org.jetbrains.dokka.model.DClass
import org.jetbrains.dokka.model.DTypeAlias
import utils.AbstractModelTest
import kotlin.test.Test
import utils.JavaCode
import utils.UsingJDK

class IsExceptionKotlinTest : AbstractModelTest("/src/main/kotlin/classes/Test.kt", "classes") {
    @UsingJDK
    @Test
    fun `isException should work for kotlin exception`(){
        inlineModelTest(
            """
            |class ExampleException(): Exception()"""
        ) {
            with((this / "classes" / "ExampleException").cast<DClass>()) {
                name equals "ExampleException"
                isException equals true
            }
        }
    }

    @UsingJDK
    @Test
    fun `isException should work for java exceptions`(){
        inlineModelTest(
            """
            |class ExampleException(): java.lang.Exception()"""
        ) {
            with((this / "classes" / "ExampleException").cast<DClass>()) {
                name equals "ExampleException"
                isException equals true
            }
        }
    }

    @UsingJDK
    @Test
    fun `isException should work for RuntimeException`(){
        inlineModelTest(
            """
            |class ExampleException(reason: String): RuntimeException(reason)"""
        ) {
            with((this / "classes" / "ExampleException").cast<DClass>()) {
                name equals "ExampleException"
                isException equals true
            }
        }
    }

    @UsingJDK
    @Test
    fun `isException should work if exception is typealiased`(){
        inlineModelTest(
            """
            |typealias ExampleException = java.lang.Exception"""
        ) {
            with((this / "classes" / "ExampleException").cast<DTypeAlias>()) {
                name equals "ExampleException"
                isException equals true
            }
        }
    }

    @UsingJDK
    @Test
    fun `isException should work if exception is extending a typaliased class`(){
        inlineModelTest(
            """
            |class ExampleException(): Exception()
            |typealias ExampleExceptionAlias = ExampleException"""
        ) {
            with((this / "classes" / "ExampleExceptionAlias").cast<DTypeAlias>()) {
                name equals "ExampleExceptionAlias"
                isException equals true
            }
        }
    }

    @Test
    fun `isException should return false for a basic class`(){
        inlineModelTest(
            """
            |class NotAnException(): Serializable"""
        ) {
            with((this / "classes" / "NotAnException").cast<DClass>()) {
                name equals "NotAnException"
                isException equals false
            }
        }
    }

    @Test
    fun `isException should return false for a typealias`(){
        inlineModelTest(
            """
            |typealias NotAnException = Serializable"""
        ) {
            with((this / "classes" / "NotAnException").cast<DTypeAlias>()) {
                name equals "NotAnException"
                isException equals false
            }
        }
    }
}

@JavaCode
class IsExceptionJavaTest: AbstractModelTest("/src/main/kotlin/java/Test.java", "java") {
    @Test
    fun `isException should work for java exceptions`(){
        inlineModelTest(
            """
            |public class ExampleException extends java.lang.Exception { }"""
        ) {
            with((this / "java" / "ExampleException").cast<DClass>()) {
                name equals "ExampleException"
                isException equals true
            }
        }
    }

    @Test
    fun `isException should work for RuntimeException`(){
        inlineModelTest(
            """
            |public class ExampleException extends java.lang.RuntimeException"""
        ) {
            with((this / "java" / "ExampleException").cast<DClass>()) {
                name equals "ExampleException"
                isException equals true
            }
        }
    }

    @Test
    fun `isException should return false for a basic class`(){
        inlineModelTest(
            """
            |public class NotAnException extends Serializable"""
        ) {
            with((this / "java" / "NotAnException").cast<DClass>()) {
                name equals "NotAnException"
                isException equals false
            }
        }
    }
}

