package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.ResourceProvider
import com.astrainteractive.empire_items.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.empire_items.util.Translations
import com.astrainteractive.empire_items.empire_items.util.resource_pack.ResourcePack
import com.astrainteractive.empire_items.empire_items.util.resource_pack.Zipper
import kotlinx.coroutines.launch
import java.io.File
private val translations: Translations
    get() = ResourceProvider.translations
class Ezip {
    val ezip = AstraLibs.registerCommand("ezip", permission = EmpirePermissions.EZIP) { sender, args ->
        AsyncHelper.launch {
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