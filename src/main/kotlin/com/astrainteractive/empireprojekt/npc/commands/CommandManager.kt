package com.astrainteractive.empireprojekt.npc.commands

import com.astrainteractive.empireprojekt.EmpirePlugin

class CommandManager {
    init {
        EmpirePlugin.instance.getCommand("emnpc")!!.tabCompleter = TabCompleter()
        EmpirePlugin.instance.getCommand("emnpc")!!.setExecutor(Commands())
    }
}