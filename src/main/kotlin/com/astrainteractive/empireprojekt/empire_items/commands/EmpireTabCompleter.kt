package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.withEntry
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.font.FontManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EmpireTabCompleter() : TabCompleter {

    val empireItems =ItemManager.getItemsIDS()

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
                FontManager.playerFonts().map{it.char}

            else -> null
        }

    }

    private fun on0Args(alias: String): List<String> {
        return listOf("emp", "emreplace", "emrepair").withEntry(alias)
    }

    private fun on1Args(alias: String,args: Array<out String>): List<String> {

        return if (alias.equals("emnpc", ignoreCase = true)) {
            listOf("create", "tp", "move", "delete", "changeskin", "select").withEntry(args.lastOrNull())
        } else if (alias.equals("emoji", ignoreCase = true)) {
            return FontManager.playerFonts().map{it.char}
        } else {
            listOf("reload", "give").withEntry(args.lastOrNull())
        }
    }

    private fun on2Args(alias: String, args: Array<out String>): List<String>? {

        if (alias.equals("emoji", ignoreCase = true)) {
            return FontManager.playerFonts().map{it.char}
        }


        return null
    }

    private fun on3Args(alias: String, args: Array<out String>): List<String>? {
        if (alias.equals("emnpc", ignoreCase = true)) {
            if (args[0].equals("create", ignoreCase = true)) {
                return listOf("<skin>")
            }
        } else if (alias.equals("emoji", ignoreCase = true)) {
            return FontManager.playerFonts().map{it.char}
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