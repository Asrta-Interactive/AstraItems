package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.registerTabCompleter
import com.astrainteractive.empire_items.api.FontApi
import com.astrainteractive.empire_items.empire_items.util.Config
import com.astrainteractive.empire_items.empire_items.util.Translations
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.random.Random

class General {
    val emojiCompleter = AstraLibs.registerTabCompleter("emoji") { sender, args ->
        return@registerTabCompleter FontApi.playerFonts().map { it.value.char }
    }

    private val edice = AstraLibs.registerCommand("edice") { sender, args ->
        if (sender !is Player)
            return@registerCommand
        val p = sender as Player
        val nearestP = Bukkit.getOnlinePlayers().filter { p.location.distance(it.location) < 30 }
        val result = Random(System.currentTimeMillis()).nextInt(1, 6 + 1)
        nearestP.forEach {
            it.sendMessage(
                Translations.instance.diceThrow.replace("%player%", p.name).replace("%value%", result.toString())
            )
        }
    }

    private val emoji = AstraLibs.registerCommand("emoji") { sender, args ->
        if (sender !is Player)
            return@registerCommand
        val chat = args.joinToString(" ").HEX()
        sender.chat(chat)
        return@registerCommand
    }
    private val empack = AstraLibs.registerCommand("empack") { sender, args ->
        if (sender is Player)
            sender.setResourcePack(Config.resourcePackLink)
        return@registerCommand
    }

}