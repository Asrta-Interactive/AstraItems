package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.plugin.Permission
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue

/**
 * Reload command handler
 */
fun CommandManager.espeed() = AstraLibs.instance.registerCommand("espeed") {
    val translation by TranslationModule
    if (!Permission.ChangeFlySpeed.hasPermission(sender)) {
        sender.sendMessage(translation.noPerms)
        return@registerCommand
    }
    if (sender!is Player) {
        Logger.warn(message="Player only command", tag = CommandManager.TAG)
        return@registerCommand
    }
    val p = sender as Player
    val speed = maxOf(0f,minOf(args.last().toFloatOrNull()?:10f,10f))/10f
    p.flySpeed = speed

}