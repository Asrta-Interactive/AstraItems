package com.astrainteractive.empire_items.empire_items.commands

import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.AstraDSLCommand
import ru.astrainteractive.astralibs.utils.convertHex
import ru.astrainteractive.astralibs.utils.then
import com.astrainteractive.empire_items.api.FontApi
import com.astrainteractive.empire_items.api.utils.EmpireUtils
import com.astrainteractive.empire_items.empire_items.gui.GuiCategories
import kotlinx.coroutines.launch
import org.bukkit.entity.Player

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
    }
}

fun CommandManager.emgui() = AstraDSLCommand.command("emgui") {
    if (sender !is Player) {
        Logger.warn("Player only command", tag = CommandManager.TAG)
        return@command
    }
    PluginScope.launch {
        GuiCategories(sender as Player).open()
    }
}

fun CommandManager.emojiBook() = AstraDSLCommand.command("emojis") {
    if (sender !is Player) {
        Logger.warn("Player only command", tag = CommandManager.TAG)
        return@command
    }
    val list = FontApi.playerFonts().mapNotNull { (id, font) ->
        font.blockSend.then(null as String?) ?: convertHex("&r${font.id}\n&r&f${font.char}&r\n")
    }
    val book = EmpireUtils.getBook("RomaRoman", convertHex("&fЭмодзи"), listOf(list.joinToString(" ")), false)
    (sender as Player).openBook(book)

}