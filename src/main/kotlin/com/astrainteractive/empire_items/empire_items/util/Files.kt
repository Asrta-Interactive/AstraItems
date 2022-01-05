package com.astrainteractive.empire_items.empire_items.util

import com.astrainteractive.astralibs.FileManager
import java.io.File

class Files {
    val configFile: FileManager =
        FileManager("config" + File.separator + "config.yml")
    val guiConfig: FileManager =
        FileManager("config"+File.separator+"gui.yml")
    val tempChunks: FileManager =
        FileManager("temp" + File.separator + "generated_chunks.yml")
}