package com.astrainteractive.empire_itemss.api.utils

import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.FontApi
import com.google.common.io.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.boss.KeyedBossBar
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.ItemMeta
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.file_manager.FileManager
import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.random.Random


fun ItemMeta.addAttribute(attr: Attribute, amount: Double, vararg slot: EquipmentSlot?) {
    slot.forEach {
        addAttributeModifier(
            attr,
            AttributeModifier(UUID.randomUUID(), attr.name, amount, AttributeModifier.Operation.ADD_NUMBER, it)
        )
    }
}

fun ConfigurationSection.getDoubleOrNull(path: String): Double? =
    if (!this.contains(path))
        null
    else getDouble(path)


fun ItemStack.setDisplayName(name: String) {
    val meta = itemMeta
    meta?.setDisplayName(name.HEX())
    itemMeta = meta
}


private fun getFilesList() = File(
    AstraLibs.instance.dataFolder.toString() + File.separator + "items" + File.separator
).listFiles()

private fun File.isYml() = Files.getFileExtension(toString()).equals("yml", ignoreCase = true)
fun getCustomItemsFiles() = getFilesList()?.filter { it.isYml() }?.map {
    FileManager("items" + File.separator + it.name)
}

fun getCustomItemsSections(section: String) = getFilesList()?.filter { it.isYml() }?.mapNotNull {
    FileManager("items" + File.separator + it.name).fileConfiguration.getConfigurationSection(section)
}

object EmpireUtils {
    private val empireItemsAPi: EmpireItemsAPI by IDependency
    private val fontAPI: FontApi by IDependency
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
        val map = empireItemsAPi.fontByID.entries.associate { ":${it.key}:" to it.value.char }.toMutableMap()
        fontAPI.getOffsets().forEach { (k, v) -> map[":$k:"] = v }
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

fun calcChance(chance: Int) = calcChance(chance.toDouble())
fun calcChance(chance: Double) = chance >= Random.nextDouble(0.0, 100.0)
fun calcChance(chance: Float) = calcChance(chance.toDouble())

fun Location.playSound(name: String?) {
    PluginScope.launch(Dispatchers.BukkitMain)  {
        this@playSound.world.playSound(this@playSound, name ?: return@launch, 2f, 1f)
    }

}

fun Location.getBiome() = world.getBiome(this)

fun getPlugin(name:String) = Bukkit.getServer().pluginManager.getPlugin(name)

fun Location.explode(power:Int) = explode(power.toDouble())
fun Location.explode(power: Double) {
    world?.createExplosion(this, power.toFloat()) ?: return
}
fun LivingEntity.addAttribute(
    attribute: Attribute,
    amount: Double,
    operation: AttributeModifier.Operation = AttributeModifier.Operation.ADD_NUMBER,
) {
    val attributeInstance = getAttribute(attribute) ?: let {
        registerAttribute(attribute)
        getAttribute(attribute)!!
    }
    attributeInstance.addModifier(
        AttributeModifier(
            UUID.randomUUID(),
            attribute.name,
            amount,
            operation
        )
    )
    if (attribute == Attribute.GENERIC_MAX_HEALTH)
        this.health = amount

}

fun KeyedBossBar.destroy() {
    Bukkit.getOnlinePlayers().forEach { removePlayer(it) }
    removeAll()
}

fun bukkitAsyncTaskTimer(delay: Long = 0L, period: Long = 20L, runnable: Runnable) =
    Bukkit.getScheduler().runTaskTimerAsynchronously(AstraLibs.instance, runnable, delay, period)