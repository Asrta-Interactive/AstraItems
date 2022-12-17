package com.astrainteractive.empire_itemss.api

import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import com.astrainteractive.empire_itemss.api.utils.EmpireUtils
import com.google.common.io.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.boss.KeyedBossBar
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.Injector
import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astralibs.utils.AstraLibsExtensions.getPersistentData
import ru.astrainteractive.astralibs.utils.HEX
import java.io.File
import java.util.*
import kotlin.random.Random

val ItemStack.empireID: String?
    get() = this.itemMeta?.getPersistentData(BukkitConstants.ASTRA_ID)

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
val empireUtils:EmpireUtils
    get() = Injector.inject()
fun String.emoji() = empireUtils.emojiPattern(this)
fun List<String>.emoji() = empireUtils.emojiPattern(this)

fun calcChance(chance: Int) = calcChance(chance.toDouble())
fun calcChance(chance: Double) = chance >= Random.nextDouble(0.0, 100.0)
fun calcChance(chance: Float) = calcChance(chance.toDouble())

fun Location.playSound(name: String?) {
    PluginScope.launch(Dispatchers.BukkitMain) {
        this@playSound.world.playSound(this@playSound, name ?: return@launch, 2f, 1f)
    }

}

fun Location.getBiome() = world.getBiome(this)

fun getPlugin(name: String) = Bukkit.getServer().pluginManager.getPlugin(name)

fun Location.explode(power: Int) = explode(power.toDouble())
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



fun bukkitAsyncTaskTimer(delay: Long = 0L, period: Long = 20L, runnable: Runnable) =
    Bukkit.getScheduler().runTaskTimerAsynchronously(AstraLibs.instance, runnable, delay, period)