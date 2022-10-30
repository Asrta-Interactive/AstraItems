package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.modules.TranslationModule
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.empire_items.util.EmpirePermissions
import com.astrainteractive.empire_items.util.Translations
import org.bukkit.entity.Player

/**
 * Reload command handler
 */
fun CommandManager.espeed() = AstraLibs.registerCommand("espeed") { sender, args ->
    if (!sender.hasPermission(EmpirePermissions.RELOAD)) {
        sender.sendMessage(TranslationModule.value.noPerms)
        return@registerCommand
    }
    if (sender!is Player) {
        Logger.warn("Player only command", tag = CommandManager.TAG)
        return@registerCommand
    }
    val p = sender as Player
    val speed = maxOf(0f,minOf(args.last().toFloatOrNull()?:10f,10f))/10f
    p.flySpeed = speed

}