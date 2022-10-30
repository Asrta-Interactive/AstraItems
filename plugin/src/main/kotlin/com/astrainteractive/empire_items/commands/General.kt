package com.astrainteractive.empire_items.commands

import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astralibs.utils.registerTabCompleter
import com.astrainteractive.empire_itemss.api.FontApi
import com.astrainteractive.empire_items.util.Translations
import com.astrainteractive.empire_items.gui.ResourcePack
import com.astrainteractive.empire_items.modules.ConfigModule
import com.astrainteractive.empire_items.modules.GuiConfigModule
import com.astrainteractive.empire_items.modules.TranslationModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.PluginScope
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
                TranslationModule.value.diceThrow.replace("%player%", p.name).replace("%value%", result.toString())
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
            PluginScope.launch(Dispatchers.IO) { ResourcePack(sender, ConfigModule.value).open() }
        return@registerCommand
    }
    private val empack_download = AstraLibs.registerCommand("empack_download") { sender, args ->
        if (sender is Player)
            sender.setResourcePack(ConfigModule.value.resourcePack.link)
        return@registerCommand
    }

}