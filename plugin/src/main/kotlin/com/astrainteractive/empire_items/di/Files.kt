package com.astrainteractive.empire_items.di

import ru.astrainteractive.astralibs.file_manager.FileManager
import java.io.File

object Files {

    val configFile: FileManager =
        FileManager("config.yml")
    val guiConfig: FileManager =
        FileManager("gui.yml")

    val enchantsModule: FileManager =
        FileManager("modules" + File.separator + "empire_enchants.yml")
}