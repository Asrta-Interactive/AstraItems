package com.astrainteractive.empire_items.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.convertHex
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.empire_items.api.font.FontManager
import com.astrainteractive.empire_items.empire_items.util.EmpireUtils
import org.bukkit.entity.Player

class EmojiBook {
    init {
        AstraLibs.registerCommand("emojis") { sender, args ->
            if (sender !is Player)
                return@registerCommand
            val list = FontManager.allFonts().mapNotNull { font ->
                if (font.blockSend)
                    null
                else
                    convertHex("&r${font.id}\n&r&f${font.char}&r\n")
            }
            val book = EmpireUtils.getBook("RomaRoman", convertHex("&fЭмодзи"), listOf(list.joinToString(" ")), false)
            sender.openBook(book)
        }
    }
}