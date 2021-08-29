package com.makeevrserg.empireprojekt.rating.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter


//erating rise <user> <amount> <reason>
//erating look <user>
class TabCompleter:TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        return when (args.size) {
            0 -> null
            1 -> listOf("rise","look")
            2 -> listOf("<amount>")
            else -> listOf("<reason>")
        }
    }
}