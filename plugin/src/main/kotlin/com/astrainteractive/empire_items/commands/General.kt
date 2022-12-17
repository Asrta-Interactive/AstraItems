package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.di.TranslationModule
import com.astrainteractive.empire_items.di.configModule
import com.astrainteractive.empire_items.di.fontApiModule
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astralibs.utils.registerTabCompleter
import com.astrainteractive.empire_itemss.api.FontApi
import com.astrainteractive.empire_items.gui.ResourcePack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import kotlin.random.Random

class General {
    val fontApi by fontApiModule
    val translation by TranslationModule
    val config by configModule
    val emojiCompleter = AstraLibs.registerTabCompleter("emoji") { sender, args ->
        return@registerTabCompleter fontApi.playerFonts().map { it.value.char }
    }

    private val edice = AstraLibs.registerCommand("edice") { sender, args ->
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

    private val emoji = AstraLibs.registerCommand("emoji") { sender, args ->
        if (sender !is Player)
            return@registerCommand
        val chat = args.joinToString(" ").HEX()
        sender.chat(chat)
        return@registerCommand
    }
    private val empack = AstraLibs.registerCommand("empack") { sender, args ->
        if (sender is Player)
            PluginScope.launch(Dispatchers.IO) { ResourcePack(sender, config).open() }
        return@registerCommand
    }
    private val empack_download = AstraLibs.registerCommand("empack_download") { sender, args ->
        if (sender is Player)
            sender.setResourcePack(config.resourcePack.link)
        return@registerCommand
    }

}