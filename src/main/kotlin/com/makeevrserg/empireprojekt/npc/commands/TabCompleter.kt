package com.makeevrserg.empireprojekt.npc.commands

import com.makeevrserg.empireprojekt.npc.NPCManager
import com.makeevrserg.empireprojekt.empirelibs.withEntry
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter:TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (args.size==1)
            return listOf("tp","create","skin","move","delete").withEntry(args[0])
        if (args.size==2 && listOf("tp","move","skin").contains(args[0]))
            return NPCManager.abstractNPCByName.keys.toList().withEntry(args[1])
        return listOf()
    }
}