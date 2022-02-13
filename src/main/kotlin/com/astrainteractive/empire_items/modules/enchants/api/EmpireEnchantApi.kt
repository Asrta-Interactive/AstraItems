package com.astrainteractive.empire_items.modules.enchants.api

import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.empire_items.api.utils.setPersistentDataType
import com.astrainteractive.empire_items.empire_items.util.Disableable
import com.astrainteractive.empire_items.modules.enchants.data.EmpireEnchantement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.random.Random


object EmpireEnchantApi : Disableable {
    private var empireEnchantments: List<EmpireEnchantement> = listOf()
    private var empireEnchantmentById: Map<String, EmpireEnchantement> = mapOf()

    fun getEnchantment(id: String) = empireEnchantmentById[id]

    override fun onEnable() {
        empireEnchantments = EmpireEnchantement.loadALl()
        empireEnchantmentById = empireEnchantments.associateBy { it.id }

    }

    override fun onDisable() {
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