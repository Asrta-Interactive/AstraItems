package com.astrainteractive.empire_items.empire_items.util

import com.astrainteractive.astralibs.FileManager
import java.io.File

class Files {
    val configFile: FileManager =
        FileManager("config.yml")
    val guiConfig: FileManager =
        FileManager("gui.yml")
    val tempChunks: FileManager =
        FileManager("temp" + File.separator + "generated_chunks.yml")
}