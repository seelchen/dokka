// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.kotlin.idea.klib

import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.Contract
import org.jetbrains.kotlin.library.KotlinLibrary
import org.jetbrains.kotlin.library.impl.KotlinLibraryImpl
import org.jetbrains.kotlin.library.metadata.KlibMetadataProtoBuf
import org.jetbrains.kotlin.library.metadata.PackageAccessHandler
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.konan.file.File as KFile


@Contract("null -> null; !null -> !null")
internal fun toSystemIndependentName(path: String?): String? {
    return if (path == null) null else FileUtilRt.toSystemIndependentName(path)
}

internal object CachingIdeKlibMetadataLoader : PackageAccessHandler {
    override fun loadModuleHeader(library: KotlinLibrary): KlibMetadataProtoBuf.Header {
        val virtualFile = getVirtualFile(library, library.moduleHeaderFile)
        return virtualFile?.let { cache.getCachedModuleHeader(virtualFile) } ?: KlibMetadataProtoBuf.Header.getDefaultInstance()
    }

    override fun loadPackageFragment(library: KotlinLibrary, packageFqName: String, partName: String): ProtoBuf.PackageFragment {
        val virtualFile = getVirtualFile(library, library.packageFragmentFile(packageFqName, partName))
        return virtualFile?.let { cache.getCachedPackageFragment(virtualFile) } ?: ProtoBuf.PackageFragment.getDefaultInstance()
    }

    private fun getVirtualFile(library: KotlinLibrary, file: KFile): VirtualFile? =
        if (library.isZipped) asJarFileSystemFile(library.libraryFile, file) else asLocalFile(file)

    private fun asJarFileSystemFile(jarFile: KFile, localFile: KFile): VirtualFile? {
        val fullPath = jarFile.absolutePath + "!" + toSystemIndependentName(localFile.path)
        return StandardFileSystems.jar().findFileByPath(fullPath)
    }

    private fun asLocalFile(localFile: KFile): VirtualFile? {
        val fullPath = localFile.absolutePath
        return StandardFileSystems.local().findFileByPath(fullPath)
    }

    private val cache
        get() = KlibLoadingMetadataCache.getInstance()

    private val KotlinLibrary.moduleHeaderFile
        get() = (this as KotlinLibraryImpl).metadata.access.layout.moduleHeaderFile

    private fun KotlinLibrary.packageFragmentFile(packageFqName: String, partName: String) =
        (this as KotlinLibraryImpl).metadata.access.layout.packageFragmentFile(packageFqName, partName)

    private val KotlinLibrary.isZipped
        get() = (this as KotlinLibraryImpl).base.access.layout.isZipped
}
