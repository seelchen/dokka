/*
 * Copyright 2014-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.dokka.it

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import java.nio.file.Path

public fun AbstractIntegrationTest.copyAndApplyGitDiff(diffFile: File) {
    copyGitDiffFileToParent(diffFile).let(::applyGitDiffFromFile)
}

public fun AbstractIntegrationTest.copyGitDiffFileToParent(originalDiffFile: File): File =
    originalDiffFile.copyTo(File(projectDir.parent, originalDiffFile.name))

public fun AbstractIntegrationTest.applyGitDiffFromFile(diffFile: File) {
    val projectGitFile = projectDir.resolve(".git")
    val git = if (projectGitFile.exists()) {
        if (projectGitFile.isFile) {
            println(".git file inside project directory exists, removing")
            removeGitFile(projectDir.toPath())
            Git.init().setDirectory(projectDir).call()
        } else {
            println(".git directory inside project directory exists, reusing")
            FileRepositoryBuilder().apply {
                isMustExist = true
                gitDir = projectDir
            }.let { Git(it.build()) }
        }
    } else {
        Git.init().setDirectory(projectDir).call()
    }
    git.apply().setPatch(diffFile.inputStream()).call()
}

private fun removeGitFile(repository: Path) =
    repository.toFile()
        .listFiles().orEmpty()
        .filter { it.name.toLowerCase() == ".git" }
        .forEach { it.delete() }


