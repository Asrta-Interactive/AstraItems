package com.makeevrserg.empireprojekt.npc.commands

import com.makeevrserg.empireprojekt.EmpirePlugin

class CommandManager {
    init {
        EmpirePlugin.instance.getCommand("emnpc")!!.tabCompleter = TabCompleter()
        EmpirePlugin.instance.getCommand("emnpc")!!.setExecutor(Commands())
    }
}