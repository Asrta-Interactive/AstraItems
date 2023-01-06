package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.util.resource_pack.ResourcePack
import com.astrainteractive.empire_items.util.resource_pack.Zipper
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue
import java.io.File
private val translations by TranslationModule
class Ezip {
    val ezip = AstraLibs.instance.registerCommand("ezip") {
        if (!sender.hasPermission(EmpirePermissions.EZIP)) return@registerCommand
        PluginScope.launch {
            sender.sendMessage(translations.zipStarted)
            ResourcePack.generate()
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