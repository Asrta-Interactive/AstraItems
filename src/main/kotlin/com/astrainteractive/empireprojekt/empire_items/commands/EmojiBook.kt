package com.astrainteractive.empireprojekt.empire_items.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empireprojekt.empire_items.api.font.FontManager
import com.astrainteractive.empireprojekt.empire_items.util.EmpireUtils
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
                    AstraUtils.HEXPattern("&r${font.id}\n&r&f${font.char}&r\n")
            }
            val book = EmpireUtils.getBook("RomaRoman", AstraUtils.HEXPattern("&fЭмодзи"), listOf(list.joinToString(" ")), false)
            sender.openBook(book)
        }
    }
}