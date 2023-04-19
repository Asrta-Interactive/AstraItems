package com.astrainteractive.empire_items.api//package com.astrainteractive.empire_items.api
//
//import com.astrainteractive.empire_items.EmpirePlugin
//import com.astrainteractive.empire_items.api.utils.*
//import com.astrainteractive.empire_items.api.utils.Disableable
//import com.astrainteractive.empire_items.empire_items.util.Translations
//import com.astrainteractive.empire_items.empire_items.util.emoji
//import org.bukkit.ChatColor
//import org.bukkit.attribute.Attribute
//import org.bukkit.inventory.ItemFlag
//import org.bukkit.inventory.ItemStack
//import org.bukkit.inventory.meta.ItemMeta
//import kotlin.random.Random
//
//object UpgradeApi : Disableable {
//
//    private var list: List<AstraUpgrade> = mutableListOf()
//    private val translations: Translations
//        get() = EmpirePlugin.translations
//    val attrMap: Map<String, String>
//        get() = mapOf(
//            "GENERIC_MAX_HEALTH" to translations.itemUpgradeMaxHealth,
//            "GENERIC_KNOCKBACK_RESISTANCE" to translations.itemUpgradeKnockbackResistance,
//            "GENERIC_ATTACK_DAMAGE" to translations.itemUpgradeAttackDamage,
//            "GENERIC_ATTACK_KNOCKBACK" to translations.itemUpgradeAttackKnockback,
//            "GENERIC_ATTACK_SPEED" to translations.itemUpgradeAttackSpeed,
//            "GENERIC_ARMOR" to translations.itemUpgradeArmor,
//            "GENERIC_ARMOR_TOUGHNESS" to translations.itemUpgradeArmorToughness,
//            "GENERIC_MOVEMENT_SPEED" to translations.itemUpgradeMovementSpeed
//        )
//
//    private fun ItemStack.isWeapon() = listOf("SWORD", "AXE").any { type.name.uppercase().contains(it) }
//
//    private fun ItemStack.isArmor() =
//        listOf("CHESTPLATE", "BOOTS", "LEGGINGS", "HELMET", "SHIELD").any { type.name.uppercase().contains(it) }
//
//    private fun Attribute.isArmorAttribute() = listOf(
//        Attribute.GENERIC_MOVEMENT_SPEED,
//        Attribute.GENERIC_ARMOR,
//        Attribute.GENERIC_ARMOR_TOUGHNESS,
//        Attribute.GENERIC_KNOCKBACK_RESISTANCE,
//        Attribute.GENERIC_MAX_HEALTH
//    ).contains(this)
//
//    private fun Attribute.isWeaponAttribute() = listOf(
//        Attribute.GENERIC_ATTACK_DAMAGE,
//        Attribute.GENERIC_ATTACK_KNOCKBACK,
//        Attribute.GENERIC_ATTACK_SPEED
//    ).contains(this)
//
//    fun addAttributes(itemStack: ItemStack, ingredient: ItemStack): ItemStack? {
//        val meta = itemStack.itemMeta!!
//        var upgraded = false
//        getAvailableUpgradesForItemStack(ingredient).forEach { upgradeModel ->
//            val upgradeAmount = Random.nextDouble(upgradeModel.addMin, upgradeModel.addMax)
//            val upgradeKey = BukkitConstants.ASTRA_ATTRIBUTE(upgradeModel.attribute)
//            var upgradeTimes = meta.getPersistentData(BukkitConstants.ASTRA_UPGRADE_TIMES) ?: 0
//            var currentAttributeAmount = meta.getPersistentData(upgradeKey) ?: 0.0
//            currentAttributeAmount += upgradeAmount
//            if (itemStack.isWeapon() && upgradeModel.attribute.isWeaponAttribute()) {
//                meta.addAttribute(upgradeModel.attribute, upgradeAmount, itemStack.type.equipmentSlot)
//                meta.setPersistentDataType(upgradeKey, currentAttributeAmount)
//                upgraded = true
//                meta.setPersistentDataType(BukkitConstants.ASTRA_UPGRADE_TIMES, ++upgradeTimes)
//            } else if (itemStack.isArmor() && upgradeModel.attribute.isArmorAttribute()) {
//                meta.addAttribute(upgradeModel.attribute, upgradeAmount, itemStack.type.equipmentSlot)
//                meta.setPersistentDataType(upgradeKey, currentAttributeAmount)
//                upgraded = true
//                meta.setPersistentDataType(BukkitConstants.ASTRA_UPGRADE_TIMES, ++upgradeTimes)
//            }
//        }
//        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
//        itemStack.itemMeta = meta
//        return if (!upgraded)
//            null
//        else
//            itemStack
//    }
//
//    fun getAvailableUpgradesForItemStack(itemStack: ItemStack): List<AstraUpgrade> =
//        list.filter { it.id == itemStack.getAstraID() }
//
//
//    override suspend fun onEnable() {
//        list = AstraUpgrade.getUpgrades() ?: listOf()
//    }
//
//    override suspend fun onDisable() {
//        list = emptyList()
//    }
//
//
//    private fun clearUpgradeLore(meta: ItemMeta): ItemMeta {
//        var lore = meta.lore ?: listOf()
//        attrMap.forEach { (_, v) ->
//            lore = lore.filter { !it.contains(v) }
//        }
//        meta.lore = lore
//        return meta
//    }
//
//    private fun Double.round(decimals: Int): Double {
//        var mult = 1.0
//        repeat(decimals) { mult *= 10 }
//        return kotlin.math.round(this * mult) / mult
//    }
//
//    fun setUpgradeLore(resultItem: ItemStack, hide: Boolean = true): ItemStack {
//        val meta = clearUpgradeLore(resultItem.itemMeta!!)
//        val lore = meta.lore ?: mutableListOf()
//        Attribute.values().forEach {
//            val upgradeKey = BukkitConstants.ASTRA_ATTRIBUTE(it)
//            if (meta.getPersistentData(upgradeKey) == null)
//                return@forEach
//            val amount = meta.getPersistentData(upgradeKey)?.round(3) ?: return@forEach
//            val key = attrMap[it.name]
//            val color = translations.itemUpgradeAmountColor
//            val colorMagic = if (hide) ChatColor.MAGIC else translations.itemUpgradeAmountColor
//            lore.add("$key: $color$colorMagic$amount")
//        }
//        meta.lore = lore.emoji()
//        resultItem.itemMeta = meta
//
//        return resultItem
//
//    }
//}