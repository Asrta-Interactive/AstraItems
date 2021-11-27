package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empireprojekt.empire_items.gui.GuiCategories
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
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