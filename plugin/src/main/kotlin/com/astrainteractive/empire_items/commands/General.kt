package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.di.configModule
import com.astrainteractive.empire_items.di.fontApiModule
import com.astrainteractive.empire_items.gui.ResourcePack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.HEX
import kotlin.random.Random

class General {
    val fontApi by fontApiModule
    val translation by TranslationModule
    val config by configModule
    val emojiCompleter = AstraLibs.instance.registerTabCompleter("emoji") {
        return@registerTabCompleter fontApi.playerFonts().map { it.value.char }
    }

    private val edice = AstraLibs.instance.registerCommand("edice") {
        if (sender !is Player)
            return@registerCommand
        val p = sender as Player
        val nearestP = Bukkit.getOnlinePlayers().filter { p.location.distance(it.location) < 30 }
        val result = Random(System.currentTimeMillis()).nextInt(1, 6 + 1)
        nearestP.forEach {
            it.sendMessage(
                translation.diceThrow.replace("%player%", p.name).replace("%value%", result.toString())
            )
        }
    }

    private val emoji = AstraLibs.instance.registerCommand("emoji") {
        val sender = this.sender
        if (sender !is Player)
            return@registerCommand
        val chat = args.joinToString(" ").HEX()
        sender.chat(chat)
        return@registerCommand
    }
    private val empack = AstraLibs.instance.registerCommand("empack") {
        val sender = this.sender
        if (sender is Player)
            PluginScope.launch(Dispatchers.IO) { ResourcePack(sender, config).open() }
        return@registerCommand
    }
    private val empack_download = AstraLibs.instance.registerCommand("empack_download") {
        val sender = this.sender
        if (sender is Player)
            sender.setResourcePack(config.resourcePack.link)
        return@registerCommand
    }

}