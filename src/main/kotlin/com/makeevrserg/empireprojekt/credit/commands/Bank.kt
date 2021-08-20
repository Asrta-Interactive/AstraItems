package com.makeevrserg.empireprojekt.credit.commands

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.credit.BankAPI
import com.makeevrserg.empireprojekt.credit.CreditAPI
import com.makeevrserg.empireprojekt.empire_items.util.EmpirePermissions
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Bank: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return true

        if (!sender.hasPermission(EmpirePermissions.BANK)){
            sender.sendMessage(EmpirePlugin.translations.NO_PERMISSION)
            return true
        }
        if (args.isEmpty()){
            sender.sendMessage(EmpirePlugin.translations.WRONG_ARGS)
            return true
        }
        if (args[0].equals("deposit",ignoreCase = true)) {
            BankAPI.depositMoney(sender, args.getOrNull(1))
            return true
        }
        if (args[0].equals("withdraw",ignoreCase = true)) {
            BankAPI.withdrawMoney(sender, args.getOrNull(1))
            return true
        }


        return false

    }



}