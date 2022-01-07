package com.astrainteractive.empire_items.empire_items.util

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.empire_items.empire_items.api.font.FontManager
import com.astrainteractive.empire_items.empire_items.api.utils.getDoubleOrNull
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
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


    fun emojiPattern(lines: List<String>): List<String> = lines.map { emojiPattern(it) }

    fun emojiPattern(_line: String): String {
        val map = FontManager.fontById().toMutableMap()
        FontManager.getOffsets().forEach { (k, v) -> map[":$k:"] = v }
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

fun calcChance(chance:Int) = chance>=Random(System.currentTimeMillis()).nextInt(0,101)
fun calcChance(chance:Double) = chance>=Random(System.currentTimeMillis()).nextDouble(0.0,101.0)
fun calcChance(chance:Float) = chance>=Random(System.currentTimeMillis()).nextDouble(0.0,101.0)

fun Location.playSound(name:String?){
    this.world.playSound(this,name?:return,1f,1f)
}
fun Location.getBiome()= world.getBiome(this)


