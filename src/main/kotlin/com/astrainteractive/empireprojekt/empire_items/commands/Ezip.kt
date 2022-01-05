package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.resource_pack.ResourcePack
import com.astrainteractive.empireprojekt.empire_items.util.resource_pack.Zipper
import java.io.File

class Ezip {
    init {
        AstraLibs.registerCommand("ezip", permission = "Astra_Zip") { sender, args ->
            runAsyncTask {
                sender.sendMessage(EmpirePlugin.translations.zipStarted)
                ResourcePack.generate()
                if (Zipper.zipAll(
                    AstraLibs.instance.dataFolder.toString() + File.separator + "pack",
                    AstraLibs.instance.dataFolder.toString() + File.separator + "pack" + File.separator + "pack.zip"
                ))
                sender.sendMessage(EmpirePlugin.translations.zipSuccess)
                else
                    sender.sendMessage(EmpirePlugin.translations.zipError)
            }
        }
    }
}