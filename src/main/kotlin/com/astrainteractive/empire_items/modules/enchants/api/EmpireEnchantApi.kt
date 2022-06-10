package com.astrainteractive.empire_items.modules.enchants.api

import com.astrainteractive.astralibs.AstraYamlParser
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.api.utils.setPersistentDataType
import com.astrainteractive.empire_items.api.utils.IManager
import com.astrainteractive.empire_items.modules.enchants.data.EmpireEnchantment
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class PotionEnchant(
    val potionEffectType: String,
    val id: String,
    val itemTypes: List<EnchantItemType>
) {
    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @kotlinx.serialization.Serializable
    enum class EnchantItemType {
        ARMOR, SWORDS, AXES, PICKAXES;

        val getList: List<Material>
            get() {
                return when (this) {
                    ARMOR -> EmpireEnchantApi.armorItems
                    SWORDS -> EmpireEnchantApi.swords
                    AXES -> EmpireEnchantApi.axes
                    PICKAXES -> EmpireEnchantApi.pickaxes
                }
            }
    }

    companion object {
        fun get(): List<PotionEnchant> {
            val s = FileManager("modules/empire_enchants.yml").getConfig().getConfigurationSection("potion_enchants")
                ?: return listOf()
            return s.getKeys(false).mapNotNull { key ->
                val section = s.getConfigurationSection(key)!!
                AstraYamlParser.configurationSectionToClass<PotionEnchant>(section)
            }
        }
    }
}

object EmpireEnchantApi : IManager {
    private var empireEnchantments: List<EmpireEnchantment> = listOf()
    private var empireEnchantmentById: Map<String, EmpireEnchantment> = mapOf()
    var potionEffectEnchants: List<PotionEnchant> = listOf()

    fun getEnchantment(id: String) = empireEnchantmentById[id]

    override suspend fun onEnable() {
        empireEnchantments = EmpireEnchantment.loadALl()
        empireEnchantmentById = empireEnchantments.associateBy { it.id }
        potionEffectEnchants = PotionEnchant.get()
        println(potionEffectEnchants)

    }

    override suspend fun onDisable() {
        empireEnchantments = emptyList()
        empireEnchantmentById = emptyMap()

    }

    fun getEnchantementLevel(tableLevel: Int, maxLevel: Int): Int {
        fun calc(i: Int): Int {
            val a = maxLevel / (3 - tableLevel)
            return a + a % 2
        }

        val maxV = calc(tableLevel)
        val minV = if (tableLevel == 0) 0 else calc(tableLevel - 1)
        return if (tableLevel == 0)
            Random.nextInt(minV, maxV + 1)
        else Random.nextInt(minV, maxV + 1)
    }

    val axes: List<Material>
        get() = listOf(
            Material.NETHERITE_AXE,
            Material.DIAMOND_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.STONE_AXE,
            Material.WOODEN_AXE,
        )
    val pickaxes: List<Material>
        get() = listOf(
            Material.NETHERITE_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.WOODEN_PICKAXE,
        )

    val swords: List<Material>
        get() = listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.STONE_SWORD,
            Material.WOODEN_SWORD,
        )
    val armorItems: List<Material>
        get() = listOf(
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS,

            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,

            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,

            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,

            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,

            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
        )
}

fun parseAnvilEnchant(e: PrepareAnvilEvent, enchant: BukkitConstant<Int, Int>, key: String): Int? {
    val rightItem = e.inventory.secondItem ?: return null
    val power = rightItem.itemMeta.getPersistentData(enchant) ?: return null
    val result = e.result ?: e.inventory.firstItem?.clone() ?: return null
    result.setEmpireEnchantment(enchant, power, key)
    e.result = result
    return power

}


fun ItemStack.setEmpireEnchantment(enchant: BukkitConstant<Int, Int>, power: Int, key: String) {
    setEnchantmentLore(key, "${ChatColor.GRAY}$key ${power.toRome()}")
    val meta = itemMeta
    meta.setPersistentDataType(enchant, power)
    itemMeta = meta
}

operator fun String.times(times: Int): String {
    var new = ""
    for (i in 0 until times)
        new += this
    return new
}

fun ItemStack.isWhitelisted(whitelist: List<Material>): Boolean = whitelist.contains(this.type)


fun Int.toRome(): String {
    return when (this) {
        0 -> ""
        1 -> "I"
        2 -> "II"
        3 -> "III"
        4 -> "IV"
        5 -> "V"
        6 -> "VI"
        7 -> "VII"
        8 -> "VII"
        9 -> "IX"
        10 -> "X"
        else -> "$this"
    }
}

fun ItemStack.setEnchantmentLore(key: String, lore: String) {
    val meta = itemMeta
    val oldLore = meta.lore?.filter { !it.contains(key) }?.toMutableList() ?: mutableListOf()
    oldLore.add(lore)
    meta.lore = oldLore
    itemMeta = meta
}