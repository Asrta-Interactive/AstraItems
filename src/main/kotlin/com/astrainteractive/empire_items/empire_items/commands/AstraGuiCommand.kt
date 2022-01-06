package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.empire_items.gui.GuiCategories
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import kotlinx.coroutines.launch
import org.bukkit.entity.Player

class AstraGuiCommand {
    private val cmd = AstraLibs.registerCommand("emgui") { sender, args ->
        if (sender !is Player)
            return@registerCommand
        AsyncHelper.runBackground {
            GuiCategories(sender).open()
        }
    }}