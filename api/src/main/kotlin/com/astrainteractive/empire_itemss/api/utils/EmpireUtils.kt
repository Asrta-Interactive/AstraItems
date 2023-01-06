package com.astrainteractive.empire_itemss.api.utils

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.FontApi
import com.astrainteractive.empire_itemss.api.HudOffset
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.convertHex
import java.util.regex.Matcher
import java.util.regex.Pattern






class EmpireUtils(
    empireItemsAPi: IDependency<EmpireItemsAPI>,
    fontAPI: IDependency<FontApi>
) {
    companion object {
        lateinit var instance: EmpireUtils
            private set
    }

    init {
        instance = this
    }
    private val empireItemsAPi by empireItemsAPi
    private val fontAPI by fontAPI
    private val emojiPattern = Pattern.compile(":([a-zA-Z0-9_]*):")

    fun getBook(author: String, title: String, lines: List<String>, useHex: Boolean = true): ItemStack {
        val book = ItemStack(Material.WRITTEN_BOOK)
        val meta = book.itemMeta as BookMeta
        meta.author = author
        meta.title = title
        val pages = mutableListOf<String>()
        for (line in lines) {
            var hexLine = if (useHex) emojiPattern(convertHex(line)) else line
            while (hexLine.length > 19 * 14) {
                pages.add(hexLine.substring(0, 19 * 14))
                hexLine = hexLine.substring(19 * 14)
            }
            pages.add(hexLine)
        }
        meta.pages = pages
        book.itemMeta = meta
        return book
    }


    fun emojiPattern(lines: List<String>): List<String> = lines.map { emojiPattern(it) }

    fun emojiPattern(_line: String): String {
        val map = empireItemsAPi.fontByID.entries.associate { ":${it.key}:" to it.value.char }.toMutableMap()
        HudOffset.values().forEach { map[":${it.id}:"] = it.char }
        var matcher: Matcher = emojiPattern.matcher(_line)
        var line = _line
        while (matcher.find()) {
            val emoji: String = line.substring(matcher.start(), matcher.end())
            val toReplace: String = map[emoji] ?: emoji.replace(":", "<<>>")
            line = line.replace(emoji, toReplace + "")
            matcher = emojiPattern.matcher(line)
        }

        return line.replace("<<>>", ":")
    }
}
