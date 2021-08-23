package com.makeevrserg.empireprojekt.empirelibs

import com.google.gson.JsonParser
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants
import com.makeevrserg.empireprojekt.empire_items.util.EmpireCrafts
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern


fun ConfigurationSection.getHEXString(path: String, def: String): String {
    return EmpireUtils.HEXPattern(getString(path, def)!!)
}

fun ConfigurationSection.getHEXString(path: String): String? {

    return EmpireUtils.HEXPattern(getString(path) ?: return null)
}

fun ConfigurationSection.getHEXStringList(path: String): List<String> {
    return EmpireUtils.HEXPattern(getStringList(path))
}

fun FileConfiguration.getHEXString(path: String, def: String): String {
    return EmpireUtils.HEXPattern(getString(path, def)!!)
}
fun ItemStack?.getEmpireID(): String? {
    return EmpireUtils.getEmpireID(this)
}

inline fun <reified T:kotlin.Enum<T>> T.valueOfOrNull(type:String?):T?{
    return java.lang.Enum.valueOf(T::class.java,type)
}
fun String?.getEmpireItem():ItemStack?{
    return EmpirePlugin.empireItems.empireItems[this]
}
fun String?.asEmpireItem():ItemStack?{
    return EmpirePlugin.empireItems.empireItems[this]
}

fun List<String>.withEntry(entry:String,ignoreCase:Boolean=true): List<String> {
    val list = mutableListOf<String>()
    for (line in this)
        if (line.contains(entry,ignoreCase = true))
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


fun BukkitRunnable.runTaskAsynchronously(){
    this.runTaskAsynchronously(EmpirePlugin.instance)
}

class EmpireUtils {





    class EmpireRunnable(private val function: Runnable) : BukkitRunnable() {
        override fun run() {
            function.run()
        }
    }

    companion object {
        inline fun <reified T : kotlin.Enum<T>> valueOfOrNull(key: String?): T? {
            return java.lang.Enum.valueOf(T::class.java, key)

        }
        fun getRecipeKey(id:String?): NamespacedKey? {
            id?:return null
            if (!EmpirePlugin.empireItems.empireItems.containsKey(id))
                return null
           return NamespacedKey(EmpirePlugin.instance, BetterConstants.CUSTOM_RECIPE_KEY.name+id)
        }




        fun useInCraft(item: String): MutableSet<String> {
            val itemStack = EmpirePlugin.empireItems.empireItems[item] ?: ItemStack(
                Material.getMaterial(item) ?: return mutableSetOf()
            )
            val set = mutableSetOf<String>()

            for (itemResult in EmpirePlugin.instance.recipies.keys) {
                val itemRecipies: EmpireCrafts.EmpireRecipe =
                    EmpirePlugin.instance.recipies[itemResult] ?: continue
                for (empireRecipe in itemRecipies.craftingTable)
                    if (empireRecipe.ingredientMap.values.contains(itemStack))
                        set.add(itemResult)
                for (empireRecipe in itemRecipies.furnace)
                    if (empireRecipe.input == itemStack)
                        set.add(itemResult)
            }
            return set

        }




        fun getItemStackByID(id: String): ItemStack? {
            return EmpirePlugin.empireItems.empireItems[id] ?: ItemStack(Material.getMaterial(id) ?: return null)
        }
        fun getItemStackByName(str: String): ItemStack {
            return EmpirePlugin.empireItems.empireItems[str] ?: ItemStack(Material.getMaterial(str) ?: Material.PAPER)
        }
        private fun getEmpireID(meta: ItemMeta?): String? {
            return meta?.persistentDataContainer?.get(
                BetterConstants.EMPIRE_ID.value,
                PersistentDataType.STRING
            )

        }

        fun getEmpireID(item: ItemStack?): String? {
            item?:return null
            return getEmpireID(item.itemMeta)
        }

        fun manageWithEmpireDurability(itemStack: ItemStack): ItemStack {

            val itemMeta = itemStack.itemMeta ?: return itemStack
            val damage: Short = itemStack.durability

            val maxCustomDurability: Int = itemMeta.persistentDataContainer.get(
                BetterConstants.MAX_CUSTOM_DURABILITY.value,
                PersistentDataType.INTEGER
            ) ?: return itemStack

            val empireDurability = maxCustomDurability - damage * maxCustomDurability / itemStack.type.maxDurability
            itemMeta.persistentDataContainer.set(
                BetterConstants.EMPIRE_DURABILITY.value,
                PersistentDataType.INTEGER,
                empireDurability
            )
            val d: Int = itemStack.type.maxDurability -
                    itemStack.type.maxDurability * empireDurability / maxCustomDurability
            itemStack.durability = d.toShort()
            itemStack.itemMeta = itemMeta
            return itemStack

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

        fun emojiPattern(lines:List<String>):List<String>{
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
}