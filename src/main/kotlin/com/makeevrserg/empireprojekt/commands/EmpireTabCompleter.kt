package com.makeevrserg.empireprojekt.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EmpireTabCompleter(val empireItems: List<String>) : TabCompleter {


    //Доделать
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {

        return when {
            args.isEmpty() -> on0Args()
            args.size == 1 -> on1Args()
            args.size == 3 -> on3Args(args[2])
            else -> null
        }

    }

    private fun on0Args(): List<String> {
        val list = mutableListOf<String>()
        list.add("emp")
        list.add("emreplace")
        list.add("emrepair")
        return list.toList()
    }

    private fun on1Args(): List<String> {
        val list = mutableListOf<String>()
        list.add("reload")
        list.add("give")
        return list.toList()
    }



    private fun on3Args(item: String): MutableList<String> {
        val list = mutableListOf<String>()
        for (i in empireItems)
            if (i.contains(item))
                list.add(i)
        return list
    }


}