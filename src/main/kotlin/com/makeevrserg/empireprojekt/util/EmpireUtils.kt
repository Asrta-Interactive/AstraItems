package com.makeevrserg.empireprojekt.util

import com.makeevrserg.empireprojekt.EmpirePlugin
import net.md_5.bungee.api.ChatColor
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.regex.Matcher
import java.util.regex.Pattern

class EmpireUtils {

    companion object {

        private val hexPattern =
            Pattern.compile("#[a-fA-F0-9]{6}|&#[a-fA-F0-9]{6}")

        private fun getEmpireID(meta: ItemMeta?): String? {
            return meta?.persistentDataContainer?.get(
                EmpirePlugin.plugin.empireConstants.empireID,
                PersistentDataType.STRING
            )

        }

        fun getEmpireID(item: ItemStack): String? {
            return getEmpireID(item.itemMeta)
        }


        private val emojiPattern = Pattern.compile(":([a-zA-Z0-9_]*):")

        fun emojiPattern(_line: String): String {
            val map = EmpirePlugin.plugin.empireFontImages.fontValueById
            var matcher: Matcher = emojiPattern.matcher(_line)
            var line = _line
            while (matcher.find()) {
                val emoji: String = line.substring(matcher.start(), matcher.end())
                val toReplace: String = map[emoji].toString()
                line = line.replace(emoji, toReplace + "")
                matcher = emojiPattern.matcher(line)
            }
            return line
        }

        fun HEXPattern(list: MutableList<String>?): List<String> {
            list ?: return mutableListOf()
            for (i in list.indices) list[i] = HEXPattern(list[i])
            return list
        }

        fun HEXPattern(line: String): String {
            var line = line
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
}