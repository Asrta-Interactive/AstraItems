package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empire_items.empire_items.gui.GuiCategories
import org.bukkit.entity.Player

class AstraGuiCommand {

    init {
        AstraLibs.registerCommand("emgui"){ sender, args->
            if (sender !is Player)
                return@registerCommand
            runAsyncTask {
                GuiCategories(sender).open()
            }
        }
    }

}