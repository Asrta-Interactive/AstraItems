package com.astrainteractive.empireprojekt.empire_items.util

import com.astrainteractive.astralibs.FileManager
import com.google.common.io.Files
import com.astrainteractive.empireprojekt.EmpirePlugin.Companion.instance
import java.io.File
import java.util.*

class Files {
    val configFile: FileManager =
        FileManager("config" + File.separator + "config.yml")
    val guiConfig: FileManager =
        FileManager("config"+File.separator+"gui.yml")
    val tempChunks: FileManager =
        FileManager("temp" + File.separator + "generated_chunks.yml")
}