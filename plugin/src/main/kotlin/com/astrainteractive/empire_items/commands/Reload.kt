package com.astrainteractive.empire_items.commands

import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.modules.TranslationModule
import com.astrainteractive.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.util.Translations

private val translations: Translations
    get() = TranslationModule.value
/**
 * Reload command handler
 */
fun CommandManager.reload() = AstraLibs.registerCommand("ereload") { sender, args ->
    if (!sender.hasPermission(EmpirePermissions.RELOAD)) {
        sender.sendMessage(translations.noPerms)
        return@registerCommand
    }
    sender.sendMessage(translations.reload)
    EmpirePlugin.instance.onDisable()
    EmpirePlugin.instance.onEnable()
    sender.sendMessage(translations.reloadComplete)
}

