package com.astrainteractive.empireprojekt.empire_items.util

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.HEX
import com.google.gson.JsonParser
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.font.FontManager
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
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



fun ItemStack.setDisplayName(name:String){
    val meta = itemMeta
    meta?.setDisplayName(name.HEX())
    itemMeta = meta
}

fun AstraLibs.registerCommand(
    alias: String,
    permission: String? = null,
    callback: (CommandSender, args: Array<out String>) -> Unit
) =
    AstraLibs.instance.getCommand(alias)?.setExecutor { sender, command, label, args ->
        if (permission != null && !sender.hasPermission(permission))
            return@setExecutor true
        callback(sender, args)
        return@setExecutor true
    }

fun AstraLibs.registerTabCompleter(
    alias: String,
    permission: String? = null,
    callback: (CommandSender, args: Array<out String>) -> List<String>
) =
    AstraLibs.instance.getCommand(alias)?.setTabCompleter { commandSender, command, s, strings ->
        return@setTabCompleter callback(commandSender,strings)
    }


/**
 * Utils class
 */
object EmpireUtils {





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
        val map = FontManager.fontById()
        var matcher: Matcher = emojiPattern.matcher(_line)
        var line = _line
        while (matcher.find()) {
            val emoji: String = line.substring(matcher.start(), matcher.end())
            val toReplace: String = map[emoji]?.char ?: emoji.replace(":", "<<>>")
            line = line.replace(emoji, toReplace + "")
            matcher = emojiPattern.matcher(line)
        }

        return line.replace("<<>>", ":")
    }

}