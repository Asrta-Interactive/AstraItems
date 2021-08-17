package com.makeevrserg.empireprojekt.essentials.inventorysaver

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ISTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {

        println(args)
        if (sender !is Player)
            return listOf()

        return when (args.size) {
            1 -> {
                listOf<String>("save", "load","delete")
            }
            2 -> {
                when {
                    args[0].equals("load", ignoreCase = true) || args[0].equals("delete", ignoreCase = true) -> {
                        val list = mutableListOf<String>()
                        for (key in ISCommandManager.getFile(sender).getConfig()?.getKeys(false) ?: return listOf())
                            if (key.contains(args[1],ignoreCase = true))
                                list.add(key)
                        list
                    }
                    args[0].equals("save", ignoreCase = true) -> {
                        listOf()
                    }
                    else -> listOf()
                }
            }
            else -> {
                val list = mutableListOf<String>()
                for (p in Bukkit.getOnlinePlayers())
                    list.add(p.name)
                list
            }
        }
    }
}