package com.makeevrserg.empireprojekt.rating.commands

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.rating.database.RatingDAO
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandManager : CommandExecutor {

    init {
        EmpirePlugin.instance.getCommand("erating")!!.tabCompleter = TabCompleter()
        EmpirePlugin.instance.getCommand("erating")!!.setExecutor(this)

    }

    private fun getReason(args: Array<out String>): String {
        val list = args.toMutableList()
        list.removeAt(0)
        list.removeAt(0)
        list.removeAt(0)
        return list.joinToString(" ")
    }

    private fun riseRating(args: Array<out String>): Boolean {
        val player = EmpirePlugin.instance.server.getPlayer(args[1]) ?: return false
        val amount = args[2].toIntOrNull() ?: return false
        val reason = getReason(args)
        if (reason.length > 256)
            return false

        RatingDAO().insertUserRating(player.uniqueId.toString(), amount, reason)
        return true
    }

    private fun seeRating(args: Array<out String>) {

        val player = EmpirePlugin.instance.server.getPlayer(args[1]) ?: return

        val rating = RatingDAO().getUserByUUID(player.uniqueId.toString())

        for (rat in rating)
            println(rat)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return true

        if (args.size > 3 && args[0].equals("rise", ignoreCase = true))
            if (riseRating(args))
                println("Успех")
            else
                println("Неудача")

        if (args.size == 2)
            seeRating(args)




        return false
    }
}