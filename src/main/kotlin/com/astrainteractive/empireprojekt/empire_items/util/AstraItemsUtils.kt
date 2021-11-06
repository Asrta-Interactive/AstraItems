package com.astrainteractive.empireprojekt.empire_items.util

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.HEX
import com.google.gson.JsonParser
import com.astrainteractive.empireprojekt.EmpirePlugin
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.URL
import java.util.concurrent.Future
import java.util.regex.Matcher
import java.util.regex.Pattern



fun valueOfOrNull(value: String): ItemFlag? {
    return try {
        ItemFlag.valueOf(value)
    } catch (e: IllegalArgumentException) {
        null
    }

}

fun ItemStack.setDisplayName(name:String){
    val meta = itemMeta
    meta?.setDisplayName(name.HEX())
    itemMeta = meta
}



/**
 * Utils class
 */
object EmpireUtils {



    inline fun <reified T : kotlin.Enum<T>> valueOfOrNull(key: String?): T? {
        return java.lang.Enum.valueOf(T::class.java, key)

    }



    fun getBook(author: String, title: String, lines: List<String>, useHex: Boolean = true): ItemStack {

        val book = ItemStack(Material.WRITTEN_BOOK)
        val meta = book.itemMeta as BookMeta

        meta.author = author
        meta.title = title

        val pages = mutableListOf<String>()
        for (line in lines) {
            var hexLine = if (useHex) EmpireUtils.emojiPattern(AstraUtils.HEXPattern(line)) else line
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

    private val emojiPattern = Pattern.compile(":([a-zA-Z0-9_]*):")

    fun emojiPattern(lines: List<String>): List<String> {
        val newList = mutableListOf<String>()
        for (line in lines)
            newList.add(emojiPattern(line))
        return newList
    }

    fun emojiPattern(_line: String): String {
        val map = EmpirePlugin.empireFonts._fontValueById
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