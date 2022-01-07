package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.*
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.util.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandManager {
    init {
        AstraGuiCommand()
        AstraItemCommand()
        EmojiBook()
        Emreplace()
        Ezip()
        Reload()
        General()
        ModelEngine()

    }
}