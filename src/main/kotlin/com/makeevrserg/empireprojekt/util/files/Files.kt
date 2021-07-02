package com.makeevrserg.empireprojekt.util.files

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.google.common.io.Files
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import java.io.File
import java.util.*

public class Files() {

    val configFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "config.yml"
        )
    val guiFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "gui.yml"
        )
    val dropsFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "drops.yml"
        )
    val upgradesFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "upgrades.yml"
        )
    val fontImagesFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "fonts.yml"
        )
    val blocksFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "blocks.yml"
        )

    val craftingFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "crafting.yml"
        )


    val mobsFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "mobs.yml"
        )
    val mechanicsFile: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "mechanics.yml"
        )
    val loreBooks: FileManager =
        FileManager(
            plugin,
            "config" + File.separator + "lore_books.yml"
        )
    val empireItemsFiles: MutableList<FileManager> = mutableListOf()


    init {
        getCustomItems()
    }


    private fun getFilesList() = File(
        plugin.dataFolder.toString() + File.separator + "items" + File.separator
    ).listFiles()

    private fun isYml(fileEntry: File) = Files.getFileExtension(fileEntry.toString()).equals("yml", ignoreCase = true)

    private fun getCustomItems() {
        if (plugin.dataFolder.listFiles() != null) {

            val files = getFilesList()

            if (files != null) {
                Arrays.sort(files)
                for (fileEntry in files)
                    if (isYml(fileEntry)) {
                        println(plugin.translations.LOADING_FILE +" "+ plugin.dataFolder + File.separator + "items" + File.separator + fileEntry.name)
                        empireItemsFiles.add(
                            FileManager(
                                plugin,
                                "items" + File.separator + fileEntry.name
                            )
                        )
                    }
            }
        }
    }
}