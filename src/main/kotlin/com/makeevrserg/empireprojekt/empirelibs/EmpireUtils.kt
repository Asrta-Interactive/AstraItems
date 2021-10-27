package com.makeevrserg.empireprojekt.empirelibs

import com.google.gson.JsonParser
import com.makeevrserg.empireprojekt.EmpirePlugin
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


/**
 * Converting string from file configuration to hex with default param
 */
fun ConfigurationSection.getHEXString(path: String, def: String): String {
    return EmpireUtils.HEXPattern(getString(path, def)!!)
}

/**
 * Converting string from file configuration to hex without default param
 */
fun FileConfiguration.getHEXString(path: String): String? {
    return EmpireUtils.HEXPattern(getString(path))
}

/**
 * Converting string list from file configuration to hex without default param
 */
fun ConfigurationSection.getHEXStringList(path: String): List<String> {
    return EmpireUtils.HEXPattern(getStringList(path))
}

/**
 * Converting string to hex
 */
fun String.HEX(): String {
    return EmpireUtils.HEXPattern(this)
}

fun FileConfiguration.getHEXString(path: String, def: String): String {
    return EmpireUtils.HEXPattern(getString(path, def)!!)
}


inline fun <reified T : kotlin.Enum<T>> T.valueOfOrNull(type: String?): T? {
    return java.lang.Enum.valueOf(T::class.java, type)
}


fun List<String>.withEntry(entry: String?, ignoreCase: Boolean = true): List<String> {
    val list = mutableListOf<String>()
    for (line in this)
        if (line.contains(entry ?: "", ignoreCase = true))
            list.add(line)
    return list
}

fun valueOfOrNull(value: String): ItemFlag? {
    return try {
        ItemFlag.valueOf(value)
    } catch (e: IllegalArgumentException) {
        null
    }

}

fun runAsyncTask(function: Runnable): BukkitTask? {
    return try {
        val id = System.currentTimeMillis()
        val taskRef = Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable {
            function.run()
            EmpirePlugin.onBukkitTaskEnded(id)
        })
        EmpirePlugin.onBukkitTaskAdded(id,taskRef)
        return taskRef
    } catch (e: IllegalPluginAccessException) {
        println("${ChatColor.RED} Trying to create thread while disabling")
        null
    }

}

fun callSyncMethod(function: Runnable): Future<Unit>? {

    return try {

        Bukkit.getScheduler().callSyncMethod(EmpirePlugin.instance) {
            function.run()
        }
    } catch (e: IllegalPluginAccessException) {
        println("${ChatColor.RED} Trying to create sync method while disabling")
        return null
    }
}



/**
 * Utils class
 */
object EmpireUtils {


    class EmpireRunnable(private val function: Runnable) : BukkitRunnable() {
        override fun run() {
            function.run()
        }
    }

    inline fun <reified T : kotlin.Enum<T>> valueOfOrNull(key: String?): T? {
        return java.lang.Enum.valueOf(T::class.java, key)

    }


    fun getSkinByPlayerName(player: Player? = null, name: String): Array<String>? {
        try {
            val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
            val reader = InputStreamReader(url.openStream())
            val uuid = JsonParser().parse(reader).asJsonObject.get("id").asString
            val url2 = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
            val reader2 = InputStreamReader(url2.openStream())
            val property =
                JsonParser().parse(reader2).asJsonObject.get("properties").asJsonArray.get(0).asJsonObject
            val texture = property.get("value").asString
            val signature = property.get("signature").asString
            return arrayOf(texture, signature)
        } catch (e: Exception) {
            println("Ошибка")
            try {


                val p = ((player ?: return null) as CraftPlayer).handle
                val profile = p.profile
                val property = profile.properties.get("textures").iterator().next()
                val texture = property.value
                val signature = property.signature
                return arrayOf(texture, signature)
            } catch (e: Exception) {
                println("Ошибка")
                return null
            }

        }
    }


    private val hexPattern =
        Pattern.compile("#[a-fA-F0-9]{6}|&#[a-fA-F0-9]{6}")


    fun getBook(author: String, title: String, lines: List<String>, useHex: Boolean = true): ItemStack {

        val book = ItemStack(Material.WRITTEN_BOOK)
        val meta = book.itemMeta as BookMeta

        meta.author = author
        meta.title = title

        val pages = mutableListOf<String>()
        for (line in lines) {
            var hexLine = if (useHex) EmpireUtils.emojiPattern(EmpireUtils.HEXPattern(line)) else line
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

    @JvmName("HEXPattern1")
    fun HEXPattern(_list: List<String>?): List<String> {
        val list = _list?.toMutableList() ?: return mutableListOf()
        for (i in list.indices) list[i] = HEXPattern(list[i])
        return list
    }

    fun HEXPattern(list: MutableList<String>?): List<String> {
        list ?: return mutableListOf()
        for (i in list.indices) list[i] = HEXPattern(list[i])
        return list
    }

    /**
     * Convert string to HEX #FFFFFF pattern
     */
    @JvmName("HEXPattern1")
    fun HEXPattern(line: String?): String? {
        line ?: return line
        return HEXPattern(line)
    }

    /**
     * Convert string to HEX #FFFFFF pattern
     */
    fun HEXPattern(l: String): String {
        var line = l
        var match = hexPattern.matcher(line)
        while (match.find()) {
            val color = line.substring(match.start(), match.end())
            line = line.replace(
                color, ChatColor.of(
                    if (color.startsWith("&")) color.substring(1) else color
                ).toString() + ""
            )
            match = hexPattern.matcher(line)
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', line)
    }

}