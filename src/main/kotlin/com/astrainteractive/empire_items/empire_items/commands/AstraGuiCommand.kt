package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.commands.AstraDSLCommand
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.empire_items.gui.GuiCategories
import kotlinx.coroutines.launch
import org.bukkit.entity.Player

class AstraGuiCommand {
    private val cmd = AstraDSLCommand.command("emgui") {
        if (sender !is Player)
            return@command
        AsyncHelper.launch {
            GuiCategories(sender as Player).open()
        }
    }}