package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.Damageable
import java.util.*
import kotlin.math.round
import kotlin.random.Random

class ItemUpgradeEvent : Listener {


    data class ItemUpgrade(
        val attr: String,
        val add_min: Double,
        val add_max: Double
    )

    private val _upgradesMap: MutableMap<String, List<ItemUpgrade>> = mutableMapOf()
    val upgradesMap: MutableMap<String, List<ItemUpgrade>>
        get() = _upgradesMap

    private fun initList() {
        val section: ConfigurationSection = EmpirePlugin.empireFiles.upgradesFile.getConfig() ?: return
        for (itemID in section.getKeys(false)) {
            val upgradesList: MutableList<ItemUpgrade> = mutableListOf()
            for (attribute in section.getConfigurationSection(itemID)!!.getKeys(false)) {
                val attrSect: ConfigurationSection =
                    section.getConfigurationSection(itemID)!!.getConfigurationSection(attribute)!!
                upgradesList.add(
                    ItemUpgrade(
                        attribute,
                        attrSect.getDouble("add_min", 0.0),
                        attrSect.getDouble("add_max", 0.0)
                    )
                )
            }
            _upgradesMap[itemID] = upgradesList
        }
    }

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
            ingrMeta.persistentDataContainer.get(EmpirePlugin.empireConstants.empireID, PersistentDataType.STRING)
                ?: return
        if (!_upgradesMap.containsKey(ingrID))
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
        for (itemUpgrade: ItemUpgrade in _upgradesMap[ingrID]!!) {
            val itemName = itemBefore.type.name.toLowerCase()
            if (!isArmor(itemName) && !isWeapon(itemName))
                continue

            if (!((!isArmor(itemName) && !isArmorAttr(itemUpgrade.attr))
                        || (!isWeapon(itemName) && !isWeaponAttr(itemUpgrade.attr)))
            )
                continue

            var attrAmount = resultMeta.persistentDataContainer.get(
                EmpirePlugin.empireConstants.getUpgradesMap()[itemUpgrade.attr] ?: continue,
                PersistentDataType.DOUBLE
            ) ?: 0.0
            isUpgraded = true
            val upgradeAmount = Random.nextDouble(itemUpgrade.add_min, itemUpgrade.add_max)

            attrAmount += upgradeAmount
            resultMeta.addAttributeModifier(
                Attribute.valueOf(itemUpgrade.attr),
                AttributeModifier(
                    UUID.randomUUID(),
                    itemUpgrade.attr,
                    upgradeAmount,
                    AttributeModifier.Operation.ADD_NUMBER,
                    itemResult.type.equipmentSlot
                )
            )



            if (resultMeta.getAttributeModifiers(Attribute.valueOf(itemUpgrade.attr)) != null) {
                resultMeta.persistentDataContainer.set(
                    EmpirePlugin.empireConstants.getUpgradesMap()[itemUpgrade.attr] ?: continue,
                    PersistentDataType.DOUBLE, attrAmount
                )

                resultMeta.lore = setAttrLore(resultMeta, itemUpgrade.attr, null)
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
        EmpireUtils.getEmpireID((e.inventory as AnvilInventory).getItem(1) ?: return) ?: return
        val itemStack: ItemStack = e.currentItem ?: return
        val itemMeta: ItemMeta = itemStack.itemMeta ?: return


        for (key in EmpirePlugin.empireConstants.getUpgradesMap().keys) {
            if (!itemMeta.persistentDataContainer.has(
                    EmpirePlugin.empireConstants.getUpgradesMap()[key] ?: continue,
                    PersistentDataType.DOUBLE
                )
            )
                continue
            itemMeta.lore = setAttrLore(
                itemMeta, key, itemMeta.persistentDataContainer.get(
                    EmpirePlugin.empireConstants.getUpgradesMap()[key] ?: continue,
                    PersistentDataType.DOUBLE
                )?.round(2)
            )
        }
        var amount = itemMeta.getAttributeModifiers(itemStack.type.equipmentSlot).values().size
        amount *= EmpirePlugin.config.itemUpgradeBreakMultiplier * amount
        println(amount)
        (itemMeta as Damageable).damage = amount
        itemStack.itemMeta = itemMeta
        EmpireUtils.manageWithEmpireDurability(itemStack)

        val location = e.whoClicked.location
        if (amount > itemStack.type.maxDurability) {
            e.whoClicked.world.strikeLightning(e.whoClicked.location)
           ( e.whoClicked as Player).sendMessage(EmpirePlugin.translations.ITEM_UPGRADE_UNSUCCESFULL)
            for (i in 0..2)
                e.inventory.setItem(i, null)
            e.whoClicked.closeInventory()
        }else{
            location.world!!.spawnParticle(Particle.GLOW,location.add(0.0,1.0,0.0),100)
            e.whoClicked.sendMessage(EmpirePlugin.translations.ITEM_UPGRADE_SUCCESFULL)

        }
    }

    init {
        instance.server.pluginManager.registerEvents(this, instance)
        initList()
    }

    fun onDisable() {
        PrepareAnvilEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
    }

}