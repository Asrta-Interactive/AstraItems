package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.EmpirePermissions

/**
 * Reload command handler
 */
class Reload{

    init {
        AstraLibs.registerCommand("ereload") { sender, args ->
            if (!sender.hasPermission(EmpirePermissions.RELOAD)) {
                sender.sendMessage(EmpirePlugin.translations.NO_PERMISSION)
                return@registerCommand
            }
            sender.sendMessage(EmpirePlugin.translations.RELOAD)
            EmpirePlugin.instance.onDisable()
            EmpirePlugin.instance.onEnable()
            sender.sendMessage(EmpirePlugin.translations.RELOAD_COMPLETE)
        }
    }
}