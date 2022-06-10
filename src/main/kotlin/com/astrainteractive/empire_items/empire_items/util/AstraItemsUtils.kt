package com.astrainteractive.empire_items.empire_items.util

import com.astrainteractive.astralibs.convertHex
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.FontApi
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.random.Random

/**
 * Utils class
 */
object EmpireUtils {
    private val emojiPattern = Pattern.compile(":([a-zA-Z0-9_]*):")

    fun getBook(author: String, title: String, lines: List<String>, useHex: Boolean = true): ItemStack {
        val book = ItemStack(Material.WRITTEN_BOOK)
        val meta = book.itemMeta as BookMeta
        meta.author = author
        meta.title = title
        val pages = mutableListOf<String>()
        for (line in lines) {
            var hexLine = if (useHex) EmpireUtils.emojiPattern(convertHex(line)) else line
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
        val map = EmpireItemsAPI.fontByID.entries.associate { ":${it.key}:" to it.value.char }.toMutableMap()
        FontApi.getOffsets().forEach { (k, v) -> map[":$k:"] = v }
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

fun String.emoji() = EmpireUtils.emojiPattern(this)
fun List<String>.emoji() = EmpireUtils.emojiPattern(this)

fun calcChance(chance:Int) = calcChance(chance.toDouble())
fun calcChance(chance:Double) = chance>=Random.nextDouble(0.0,100.0)
fun calcChance(chance:Float) = calcChance(chance.toDouble())

fun Location.playSound(name:String?){
    this.world.playSound(this,name?:return,2f,1f)
}
fun Location.getBiome()= world.getBiome(this)



infix fun <T> Boolean.then(param: T): T? = if (this) param else null