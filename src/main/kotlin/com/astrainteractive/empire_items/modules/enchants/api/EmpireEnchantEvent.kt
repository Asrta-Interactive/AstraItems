package com.astrainteractive.empire_items.modules.enchants.api

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.empire_items.util.calcChance
import com.astrainteractive.empire_items.modules.enchants.data.EmpireEnchantement
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack

abstract class EmpireEnchantEvent : EventListener {
    abstract val enchant: BukkitConstant<Int, Int>
    abstract val enchantKey: String
    abstract val materialWhitelist: List<Material>
    val empireEnchant: EmpireEnchantement?
        get() = EmpireEnchantApi.getEnchantment(enchant.value.key.uppercase())
    fun getEnchantLevel(itemStack: ItemStack) = itemStack.itemMeta?.getPersistentData(enchant)

    @EventHandler
    fun onAnvilEnchant(e: PrepareAnvilEvent) {
        val eEnchant = empireEnchant ?: return
        if (!eEnchant.anvilEnchantingEnabled) return
        if (e.inventory.firstItem?.isWhitelisted(materialWhitelist) != true)
            return
        val power = parseAnvilEnchant(e, enchant, enchantKey) ?: return
        e.inventory.repairCost += eEnchant.expPerLevel * power
    }

    @EventHandler
    fun onBookEnchant(e: EnchantItemEvent) {
        val eEnchant = empireEnchant ?: return
        if (!eEnchant.enchantingTableEnabled) return
        val i = e.whichButton()
        val chance = eEnchant.chances[i]
        if (!calcChance(chance)) return
        val level = EmpireEnchantApi.getEnchantementLevel(i, eEnchant.maxLevel)
        e.item.setEmpireEnchantment(enchant, level, enchantKey)
    }

    override fun onDisable() {
        EnchantItemEvent.getHandlerList().unregister(this)
        PrepareAnvilEvent.getHandlerList().unregister(this)
    }
}