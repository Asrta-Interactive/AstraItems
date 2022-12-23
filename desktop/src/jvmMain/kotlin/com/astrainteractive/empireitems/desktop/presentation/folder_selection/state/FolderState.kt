package com.astrainteractive.empireitems.desktop.presentation.folder_selection.state

import java.io.File
import java.nio.file.FileSystems

val DEFAULT_PATH = FileSystems.getDefault().getPath("").toAbsolutePath().toFile()
val TEST_PATH = FileSystems.getDefault().getPath("D:\\Minecraft Servers\\EmpireProjekt_remake\\smp\\plugins\\EmpireItems\\items").toAbsolutePath().toFile()
class FolderState(val path: File = TEST_PATH) {
    val files = path.listFiles()?.sortedByDescending {
        it.isDirectory
    } ?: emptyList<File>()
    val parent: String? = path.parentFile?.absolutePath
}
