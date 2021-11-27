package com.astrainteractive.empireprojekt.empire_items.api.upgrade

import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empireprojekt.empire_items.api.utils.addAttribute
import com.astrainteractive.empireprojekt.empire_items.api.utils.getPersistentData
import com.astrainteractive.empireprojekt.empire_items.api.utils.setPersistentDataType
import org.bukkit.ChatColor
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.random.Random

object UpgradeManager {

    var list: List<AstraUpgrade> = mutableListOf()
    val attrMap: Map<String, String>
        get() = mapOf(
            "GENERIC_MAX_HEALTH" to "Здоровье",
            "GENERIC_KNOCKBACK_RESISTANCE" to "Откидывание",
            "GENERIC_ATTACK_DAMAGE" to "Урон",
            "GENERIC_ATTACK_KNOCKBACK" to "Откидывание",
            "GENERIC_ATTACK_SPEED" to "Скорость Атаки",
            "GENERIC_ARMOR" to "Броня",
            "GENERIC_ARMOR_TOUGHNESS" to "Прочность брони",
            "GENERIC_MOVEMENT_SPEED" to "Скорость"
        )

    fun ItemStack.isWeapon() = listOf("SWORD", "AXE").any { type.name.uppercase().contains(it) }

    fun ItemStack.isArmor() =
        listOf("CHESTPLATE", "BOOTS", "LEGGINGS", "HELMET", "SHIELD").any { type.name.uppercase().contains(it) }

    fun Attribute.isArmorAttribute() = listOf(
        Attribute.GENERIC_MOVEMENT_SPEED,
        Attribute.GENERIC_ARMOR,
        Attribute.GENERIC_ARMOR_TOUGHNESS,
        Attribute.GENERIC_KNOCKBACK_RESISTANCE,
        Attribute.GENERIC_MAX_HEALTH
    ).contains(this)

    fun Attribute.isWeaponAttribute() = listOf(
        Attribute.GENERIC_ATTACK_DAMAGE,
        Attribute.GENERIC_ATTACK_KNOCKBACK,
        Attribute.GENERIC_ATTACK_SPEED
    ).contains(this)

    fun addAttributes(itemStack: ItemStack, ingredient: ItemStack): ItemStack? {
        val meta = itemStack.itemMeta!!
        var upgraded = false
        getUpgrade(ingredient).forEach { it ->

                val value = Random.nextDouble(it.addMin, it.addMax)
                val upgradeKey = BukkitConstants.ASTRA_ATTRIBUTE(it.attribute)
                var upgradeTimes = meta.getPersistentData(BukkitConstants.ASTRA_UPGRADE_TIMES) ?: 0
                var currentAttributeAmount = meta.getPersistentData(upgradeKey) ?: 0.0
                currentAttributeAmount += value
                if (itemStack.isWeapon() && it.attribute.isWeaponAttribute()) {
                    meta.addAttribute(it.attribute, value, itemStack.type.equipmentSlot)
                    meta.setPersistentDataType(upgradeKey, currentAttributeAmount)
                    upgraded = true
                    upgradeTimes++
                    meta.setPersistentDataType(BukkitConstants.ASTRA_UPGRADE_TIMES, upgradeTimes)
                }
                if (itemStack.isArmor() && it.attribute.isArmorAttribute()) {
                    meta.addAttribute(it.attribute, value, itemStack.type.equipmentSlot)
                    meta.setPersistentDataType(upgradeKey, currentAttributeAmount)
                    upgraded = true
                    upgradeTimes++
                    meta.setPersistentDataType(BukkitConstants.ASTRA_UPGRADE_TIMES, upgradeTimes)
                }

        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        itemStack.itemMeta = meta
        return if (!upgraded)
            null
        else
            itemStack
    }

    fun getUpgrade(itemStack: ItemStack): List<AstraUpgrade> {
        val id = itemStack.getAstraID()
        val upgrades = list.filter { it.id == id }
        return upgrades
    }

    fun loadUpgrade() {
        list = AstraUpgrade.getUpgrades() ?: listOf()

    }


    fun clearUpgradeLore(meta: ItemMeta): ItemMeta {
        val lore = meta.lore?: mutableListOf()
        attrMap.forEach { (k, v) ->
            lore.toList().forEachIndexed { i, s ->
                if (s.contains(v))
                    lore.remove(s)
            }
        }
        meta.lore = lore

        return meta
    }

    private fun Double.round(decimals: Int): Double {
        var mult = 1.0
        repeat(decimals) { mult *= 10 }
        return kotlin.math.round(this * mult) / mult
    }

    fun setUpgradeLore(resultItem: ItemStack, hide: Boolean = true): ItemStack {
        val meta = clearUpgradeLore(resultItem.itemMeta!!)
        val lore = meta.lore ?: mutableListOf()
        Attribute.values().forEach {
            val upgradeKey = BukkitConstants.ASTRA_ATTRIBUTE(it)
            var currentAttributeAmount = meta.getPersistentData(upgradeKey) ?: return@forEach
            if (hide)
                lore.add("${EmpirePlugin.translations.ITEM_UPGRADE_AMOUNT_COLOR}${attrMap[it.name]}: ${ChatColor.MAGIC}${currentAttributeAmount.round(3)}")
            else
                lore.add("${EmpirePlugin.translations.ITEM_UPGRADE_AMOUNT_COLOR}${attrMap[it.name]}: ${currentAttributeAmount.round(3)}")
        }
        meta.lore = lore
        resultItem.itemMeta = meta

        return resultItem

    }
}