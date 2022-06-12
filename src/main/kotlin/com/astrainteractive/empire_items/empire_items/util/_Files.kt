package com.astrainteractive.empire_items.empire_items.util

import com.astrainteractive.astralibs.FileManager
import java.io.File

val Files: _Files
    get() = _Files.instance

class _Files {
    companion object {
        lateinit var instance: _Files
            private set
    }

    init {
        instance = this
    }

    val configFile: FileManager =
        FileManager("config.yml")
    val guiConfig: FileManager =
        FileManager("gui.yml")
    val tempChunks: FileManager =
        FileManager("temp" + File.separator + "generated_chunks.yml")

    val enchantsModule: FileManager =
        FileManager("modules" + File.separator + "empire_enchants.yml")
}