package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.di.empireItemsApiModule
import com.astrainteractive.empire_items.plugin.Permission
import com.astrainteractive.empire_items.zipper.ResourcePack
import com.astrainteractive.empire_items.zipper.Zipper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue
import java.io.File
private val translations by TranslationModule
class Ezip {
    val ezip = AstraLibs.instance.registerCommand("ezip") {
        if (!Permission.Zip.hasPermission(sender)) return@registerCommand
        PluginScope.launch(Dispatchers.IO) {
            sender.sendMessage(translations.zipStarted)
            ResourcePack.generate(empireItemsApiModule.value)
            if (Zipper.zipAll(
                    AstraLibs.instance.dataFolder.toString() + File.separator + "pack",
                    AstraLibs.instance.dataFolder.toString() + File.separator + "pack" + File.separator + "pack.zip"
                )
            )
                sender.sendMessage(translations.zipSuccess)
            else
                sender.sendMessage(translations.zipError)
        }
    }

}