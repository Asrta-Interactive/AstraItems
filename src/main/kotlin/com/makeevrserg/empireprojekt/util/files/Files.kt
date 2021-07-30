package com.makeevrserg.empireprojekt.util.files

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.google.common.io.Files
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import java.io.File
import java.util.*

public class Files() {

    val configFile: FileManager =
        FileManager("config" + File.separator + "config.yml")
    val guiFile: FileManager =
        FileManager("config" + File.separator + "gui.yml")
    val dropsFile: FileManager =
        FileManager("config" + File.separator + "drops.yml")
    val upgradesFile: FileManager =
        FileManager("config" + File.separator + "upgrades.yml")
    val fontImagesFile: FileManager =
        FileManager("config" + File.separator + "fonts.yml")
    val blocksFile: FileManager =
        FileManager("config" + File.separator + "blocks.yml")
    val craftingFile: FileManager =
        FileManager("config" + File.separator + "crafting.yml")
    val mobsFile: FileManager =
        FileManager("config" + File.separator + "mobs.yml")
    val mechanicsFile: FileManager =
        FileManager("config" + File.separator + "mechanics.yml")
    val loreBooks: FileManager =
        FileManager("config" + File.separator + "lore_books.yml")
    val npcs: FileManager =
        FileManager("config" + File.separator + "npcs.yml")
    val tempChunks: FileManager =
        FileManager("temp" + File.separator + "generated_chunks.yml")
    val empireItemsFiles: MutableList<FileManager> = mutableListOf()

    val _soundsFile: FileManager = FileManager("config" + File.separator + "sounds.yml")


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