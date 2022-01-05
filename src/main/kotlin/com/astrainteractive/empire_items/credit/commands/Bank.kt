package com.astrainteractive.empire_items.credit.commands

import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.credit.BankAPI
import com.astrainteractive.empire_items.empire_items.util.EmpirePermissions
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Bank: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return true

        if (!sender.hasPermission(EmpirePermissions.BANK)){
            sender.sendMessage(EmpirePlugin.translations.noPerms)
            return true
        }
        if (args.isEmpty()){
            sender.sendMessage(EmpirePlugin.translations.wrongArgs)
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