package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.util.EmpirePermissions
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Reload command handler
 */
fun CommandManager.reload() = AstraLibs.registerCommand("ereload") { sender, args ->
    if (!sender.hasPermission(EmpirePermissions.RELOAD)) {
        sender.sendMessage(EmpirePlugin.translations.noPerms)
        return@registerCommand
    }
    sender.sendMessage(EmpirePlugin.translations.reload)
    EmpirePlugin.instance.onDisable()
    EmpirePlugin.instance.onEnable()
    sender.sendMessage(EmpirePlugin.translations.reloadComplete)
}

