package com.astrainteractive.empire_items.commands

import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.di.GuiConfigModule
import com.astrainteractive.empire_items.di.empireUtilsModule
import com.astrainteractive.empire_items.di.fontApiModule
import com.astrainteractive.empire_items.gui.categories.GuiCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.convertHex

class CommandManager {
    companion object {
        const val TAG = "CommandManager"
    }

    init {
        emgui()
        emojiBook()
        AstraItemCommand()
        emReplace()
        espeed()
        reload()
        Ezip()
        General()
        ModelEngine()
        villagerInventory()
        villagerInventoryAutoComplete()
        AstraLibs.instance.registerCommand("edisable") {
            EmpirePlugin.instance.apply {
                onDisable()
            }

        }

    }
}

fun CommandManager.emgui() = AstraLibs.instance.registerCommand("emgui") {
    val sender = this.sender
    val guiConfig by GuiConfigModule
    if (sender !is Player) {
        Logger.warn(message="Player only command", tag = CommandManager.TAG)
        return@registerCommand
    }
    PluginScope.launch(Dispatchers.IO) { GuiCategories(sender, guiConfig = guiConfig).open() }
}

fun CommandManager.emojiBook() = AstraLibs.instance.registerCommand("emojis") {
    val fontApi by fontApiModule
    val empireUtils by empireUtilsModule
    if (sender !is Player) {
        Logger.warn(message="Player only command", tag = CommandManager.TAG)
        return@registerCommand
    }
    val list = fontApi.playerFonts().mapNotNull { (id, font) ->
        if (font.blockSend) null as String? else convertHex("&r${font.id}\n&r&f${font.char}&r\n")
    }
    val book = empireUtils.getBook("RomaRoman", convertHex("&fЭмодзи"), listOf(list.joinToString(" ")), false)
    (sender as Player).openBook(book)

}