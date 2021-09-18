package com.makeevrserg.empireprojekt.empire_items.events.upgrades

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.math.round
import kotlin.random.Random

class ItemUpgradeEvent : IEmpireListener {


    companion object {
        val attrMap: Map<String, String> = mapOf(
            "GENERIC_MAX_HEALTH" to "Здоровье",
            "GENERIC_KNOCKBACK_RESISTANCE" to "Откидывание",
            "GENERIC_ATTACK_DAMAGE" to "Урон",
            "GENERIC_ATTACK_KNOCKBACK" to "Откидывание",
            "GENERIC_ATTACK_SPEED" to "Скорость Атаки",
            "GENERIC_ARMOR" to "Броня",
            "GENERIC_ARMOR_TOUGHNESS" to "Прочность брони",
            "GENERIC_MOVEMENT_SPEED" to "Скорость"

        )
    }


    private fun setAttrLore(itemMeta: ItemMeta, attr: String, amount: Double?): MutableList<String> {
        val lore: MutableList<String> = itemMeta.lore ?: mutableListOf()

        lore.add(EmpireUtils.HEXPattern("${EmpirePlugin.translations.ITEM_UPGRADE_NAME_COLOR}${attrMap[attr] ?: attr}: ${EmpirePlugin.translations.ITEM_UPGRADE_AMOUNT_COLOR}${amount ?: "&kAAA"}"))
        for (i in 0 until lore.size - 1)
            if (lore[i].contains(attrMap[attr] ?: attr)) {
                lore.removeAt(i)
                break
            }
        return lore
    }

    private fun Double.round(decimals: Int): Double {
        var mult = 1.0
        repeat(decimals) { mult *= 10 }
        return round(this * mult) / mult
    }

    @EventHandler
    fun onAnvilEvent(e: PrepareAnvilEvent) {


        val itemBefore: ItemStack = e.inventory.getItem(0) ?: return
        val ingredient: ItemStack = e.inventory.getItem(1) ?: return

        if (ingredient.amount > 1)
            return

        val ingrMeta: ItemMeta = ingredient.itemMeta ?: return
        val ingrID: String =
            ingrMeta.persistentDataContainer.get(BetterConstants.EMPIRE_ID.value, PersistentDataType.STRING)
                ?: return
        if (!EmpirePlugin.upgradeManager._upgradesMap.containsKey(ingrID))
            return
        val itemResult = itemBefore.clone()
        val resultMeta = itemResult.itemMeta ?: return
        var isUpgraded = false

        fun isArmor(itemTypeName: String): Boolean {
            return (
                    itemTypeName.contains("chestplate") ||
                            itemTypeName.contains("boots") ||
                            itemTypeName.contains("leggings") ||
                            itemTypeName.contains("helmet") ||
                            itemTypeName.contains("shield"))
        }

        fun isWeapon(itemTypeName: String): Boolean {
            return (itemTypeName.contains("sword") ||
                    itemTypeName.contains("axe"))
        }

        fun isArmorAttr(attr: String): Boolean {
            return (attr == Attribute.GENERIC_MOVEMENT_SPEED.name ||
                    attr == Attribute.GENERIC_ARMOR.name ||
                    attr == Attribute.GENERIC_ARMOR_TOUGHNESS.name ||
                    attr == Attribute.GENERIC_KNOCKBACK_RESISTANCE.name ||
                    attr == Attribute.GENERIC_MAX_HEALTH.name)
        }

        fun isWeaponAttr(attr: String): Boolean {
            return (attr == Attribute.GENERIC_ATTACK_DAMAGE.name ||
                    attr == Attribute.GENERIC_ATTACK_KNOCKBACK.name ||
                    attr == Attribute.GENERIC_ATTACK_SPEED.name)
        }
        for (itemUpgrade in EmpirePlugin.upgradeManager._upgradesMap[ingrID]!!) {
            val itemName = itemBefore.type.name.toLowerCase()
            if (!isArmor(itemName) && !isWeapon(itemName))
                continue

            if (!((!isArmor(itemName) && !isArmorAttr(itemUpgrade.attribute.name))
                        || (!isWeapon(itemName) && !isWeaponAttr(itemUpgrade.attribute.name)))
            )
                continue


            var attrAmount = resultMeta.persistentDataContainer.get(
                BetterConstants.valueOf(itemUpgrade.attribute.name).value ?: continue,
                PersistentDataType.DOUBLE
            ) ?: 0.0
            isUpgraded = true
            val upgradeAmount = Random.nextDouble(itemUpgrade.add_min, itemUpgrade.add_max)

            attrAmount += upgradeAmount
            resultMeta.addAttributeModifier(
                itemUpgrade.attribute,
                AttributeModifier(
                    UUID.randomUUID(),
                    itemUpgrade.attribute.name,
                    upgradeAmount,
                    AttributeModifier.Operation.ADD_NUMBER,
                    itemResult.type.equipmentSlot
                )
            )



            if (resultMeta.getAttributeModifiers(itemUpgrade.attribute) != null) {
                resultMeta.persistentDataContainer.set(
                    BetterConstants.valueOf(itemUpgrade.attribute.name).value ?: continue,
                    PersistentDataType.DOUBLE, attrAmount
                )

                resultMeta.lore = setAttrLore(resultMeta, itemUpgrade.attribute.name, null)
                resultMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                itemResult.itemMeta = resultMeta

            }

        }

        if (!isUpgraded) {
            e.result = null
            return
        }
        e.result = itemResult
        e.inventory.repairCost = 3

    }

    @EventHandler
    fun inventoryClickEvent(e: InventoryClickEvent) {
        if (e.inventory !is AnvilInventory)
            return
        val view: InventoryView = e.view
        val rawSlot = e.rawSlot
        if (rawSlot != view.convertSlot(rawSlot))
            return
        if (rawSlot != 2)
            return
        ItemsAPI.getEmpireID((e.inventory as AnvilInventory).getItem(1) ?: return) ?: return
        val itemStack: ItemStack = e.currentItem ?: return
        val itemMeta: ItemMeta = itemStack.itemMeta ?: return


        for (key in BetterConstants.values()) {
            if (!itemMeta.persistentDataContainer.has(
                    key.value ?: continue,
                    PersistentDataType.DOUBLE
                )
            )
                continue
            itemMeta.lore = setAttrLore(
                itemMeta, key.name, itemMeta.persistentDataContainer.get(
                    key.value ?: continue,
                    PersistentDataType.DOUBLE
                )?.round(2)
            )
        }
        var amount = itemMeta.getAttributeModifiers(itemStack.type.equipmentSlot).values().size
        amount *= EmpirePlugin.empireConfig.itemUpgradeBreakMultiplier * amount
        (itemMeta as Damageable).damage = amount
        itemStack.itemMeta = itemMeta
        ItemsAPI.manageWithEmpireDurability(itemStack)

        val location = e.whoClicked.location
        if (amount > itemStack.type.maxDurability) {
            e.whoClicked.world.strikeLightning(e.whoClicked.location)
            (e.whoClicked as Player).sendMessage(EmpirePlugin.translations.ITEM_UPGRADE_UNSUCCESFULL)
            for (i in 0..2)
                e.inventory.setItem(i, null)
            e.whoClicked.closeInventory()
        } else {
            location.world!!.spawnParticle(Particle.GLOW, location.add(0.0, 1.0, 0.0), 100)
            e.whoClicked.sendMessage(EmpirePlugin.translations.ITEM_UPGRADE_SUCCESFULL)

        }
    }


    override fun onDisable() {
        PrepareAnvilEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
    }

}