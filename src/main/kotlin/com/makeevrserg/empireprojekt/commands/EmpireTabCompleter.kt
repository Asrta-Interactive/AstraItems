package com.makeevrserg.empireprojekt.commands

import com.makeevrserg.empireprojekt.npcs.NPCManager
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.mobs.EmpireMobsManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EmpireTabCompleter() : TabCompleter {

    val empireItems = EmpirePlugin.empireItems.empireItems.keys.toList()

    //Доделать
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {

        return when {
            args.isEmpty() -> on0Args()
            args.size == 1 -> on1Args(alias)
            args.size == 2 -> on2Args(alias, args)
            args.size == 3 -> on3Args(alias, args)
            else -> null
        }

    }

    private fun on0Args(): List<String> {
        return listOf("emp", "emreplace", "emrepair")
    }

    private fun on1Args(alias: String): List<String> {

        return if (alias.equals("emnpc", ignoreCase = true)) {
            listOf("create", "tp", "move", "delete", "changeskin", "select")
        } else if (alias.equals("emspawn", ignoreCase = true)) {
            EmpireMobsManager.mobById.keys.toList()
        } else {
            listOf("reload", "give")
        }
    }

    private fun on2Args(alias: String, args: Array<out String>): List<String>? {
        if (alias.equals("emnpc", ignoreCase = true)) {
            if (args[0].equals("create", ignoreCase = true)) {
                return listOf("<id>")
            } else if (listOf("create", "tp", "delete", "changeskin", "move").contains(args[0])) {
                return NPCManager.NPCMap.keys.toList()
            }
        }

        return null
    }

    private fun on3Args(alias: String, args: Array<out String>): List<String>? {
        if (alias.equals("emnpc", ignoreCase = true)) {
            if (args[0].equals("create", ignoreCase = true)) {
                return listOf("<skin>")
            }
        } else {
            if (!args[0].equals("give", ignoreCase = true))
                return null

            val list = mutableListOf<String>()
            val item = args[2]
            for (i in empireItems)
                if (i.contains(item))
                    list.add(i)
            return list
        }
        return null

    }


}