package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.plugin.Permission
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue

private val translations by TranslationModule
/**
 * Reload command handler
 */
fun CommandManager.reload() = AstraLibs.instance.registerCommand("ereload") {
    if (!Permission.Reload.hasPermission(sender)) {
        sender.sendMessage(translations.noPerms)
        return@registerCommand
    }
    sender.sendMessage(translations.reload)
    EmpirePlugin.instance.onDisable()
    EmpirePlugin.instance.reload()
    EmpirePlugin.instance.onEnable()
    sender.sendMessage(translations.reloadComplete)
}

