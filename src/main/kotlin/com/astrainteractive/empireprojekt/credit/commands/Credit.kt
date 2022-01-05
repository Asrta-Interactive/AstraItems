package com.astrainteractive.empireprojekt.credit.commands

import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.credit.CreditAPI
import com.astrainteractive.empireprojekt.empire_items.util.EmpirePermissions
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Credit:CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player)
            return true


        if (!sender.hasPermission(EmpirePermissions.CREDIT)){
            sender.sendMessage(EmpirePlugin.translations.noPerms)
            return true
        }
        if (args.size==2 && args[0].equals("take",ignoreCase = true)) {
            CreditAPI.giveCredit(sender, args[1])
            return true
        }

        if (args.size==1 && args[0].equals("repay",ignoreCase = true)) {
            CreditAPI.repayCredit(sender)
            return true
        }
        sender.sendMessage(EmpirePlugin.translations.wrongArgs)
        return false
    }



}