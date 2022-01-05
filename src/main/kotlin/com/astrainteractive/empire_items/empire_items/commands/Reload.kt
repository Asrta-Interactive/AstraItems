package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.empire_items.util.EmpirePermissions

/**
 * Reload command handler
 */
class Reload{

    init {
        AstraLibs.registerCommand("ereload") { sender, args ->
            if (!sender.hasPermission(EmpirePermissions.RELOAD)) {
                sender.sendMessage(EmpirePlugin.translations.noPerms)
                return@registerCommand
            }
            sender.sendMessage(EmpirePlugin.translations.reload)
            EmpirePlugin.instance.onDisable()
            EmpirePlugin.instance.onEnable()
            sender.sendMessage(EmpirePlugin.translations.reloadComplete)
        }
    }
}