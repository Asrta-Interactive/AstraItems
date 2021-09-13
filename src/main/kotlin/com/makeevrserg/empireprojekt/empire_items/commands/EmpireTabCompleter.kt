package com.makeevrserg.empireprojekt.empire_items.commands

import com.makeevrserg.empireprojekt.npc.NPCManager
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.withEntry
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
            args.isEmpty() -> on0Args(alias)
            args.size == 1 -> on1Args(alias,args)
            args.size == 2 -> on2Args(alias, args)
            args.size == 3 -> on3Args(alias, args)

            (alias.equals("emoji", ignoreCase = true)) ->
                EmpirePlugin.empireFonts.playerFonts.values.toList()

            else -> null
        }

    }

    private fun on0Args(alias: String): List<String> {
        return listOf("emp", "emreplace", "emrepair").withEntry(alias)
    }

    private fun on1Args(alias: String,args: Array<out String>): List<String> {

        return if (alias.equals("emnpc", ignoreCase = true)) {
            listOf("create", "tp", "move", "delete", "changeskin", "select").withEntry(args.lastOrNull())
        } else if (alias.equals("erandomitem", ignoreCase = true)) {
            val list = EmpirePlugin.instance.randomItems.getList()
            list.withEntry(args.lastOrNull())
        } else if (alias.equals("emoji", ignoreCase = true)) {
            return EmpirePlugin.empireFonts.playerFonts.values.toList()
        } else {
            listOf("reload", "give").withEntry(args.lastOrNull())
        }
    }

    private fun on2Args(alias: String, args: Array<out String>): List<String>? {

        if (alias.equals("erandomitem", ignoreCase = true)) {
            val list = EmpirePlugin.instance.randomItems.getList()
            val arg = args[0]
            val newList = mutableListOf<String>()
            for (id in list)
                if (id.contains(arg, ignoreCase = true))
                    newList.add(arg)
            return newList.withEntry(args.lastOrNull())
        } else if (alias.equals("emoji", ignoreCase = true)) {
            return EmpirePlugin.empireFonts.playerFonts.values.toList()
        }


        return null
    }

    private fun on3Args(alias: String, args: Array<out String>): List<String>? {
        if (alias.equals("emnpc", ignoreCase = true)) {
            if (args[0].equals("create", ignoreCase = true)) {
                return listOf("<skin>")
            }
        } else if (alias.equals("emoji", ignoreCase = true)) {
            return EmpirePlugin.empireFonts.playerFonts.values.toList()
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