package com.astrainteractive.empireprojekt.empire_items.util

import com.astrainteractive.astralibs.FileManager
import com.google.common.io.Files
import com.astrainteractive.empireprojekt.EmpirePlugin.Companion.instance
import java.io.File
import java.util.*

public class Files() {

    val configFile: FileManager =
        FileManager("config" + File.separator + "config.yml")
    val guiConfig: FileManager =
        FileManager("config"+File.separator+"gui.yml")
    val dropsFile: FileManager =
        FileManager("config" + File.separator + "drops.yml")
    val tempChunks: FileManager =
        FileManager("temp" + File.separator + "generated_chunks.yml")

    val empireItemsFiles: MutableList<FileManager> = mutableListOf()


    init {
        getCustomItems()
    }


    private fun getFilesList() = File(
        instance.dataFolder.toString() + File.separator + "items" + File.separator
    ).listFiles()

    private fun isYml(fileEntry: File) = Files.getFileExtension(fileEntry.toString()).equals("yml", ignoreCase = true)

    private fun getCustomItems() {
        if (instance.dataFolder.listFiles() != null) {

            val files = getFilesList()

            if (files != null) {
                Arrays.sort(files)
                for (fileEntry in files)
                    if (isYml(fileEntry)) {

                        empireItemsFiles.add(
                            FileManager(
                                "items" + File.separator + fileEntry.name
                            )
                        )
                    }
            }
        }
    }
}