package com.astrainteractive.empire_items.api.enchants

import ru.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.enchants.models.GenericEnchant
import com.astrainteractive.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.api.utils.calcChance
import com.astrainteractive.empire_items.api.utils.getPersistentData
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack

abstract class EmpireEnchantEvent : EventListener {
    abstract val enchant: BukkitConstant<Int, Int>
    abstract val enchantKey: String
    abstract val materialWhitelist: List<Material>
    abstract val empireEnchant: GenericEnchant.IGenericEnchant

    fun getEnchantLevel(itemStack: ItemStack) = itemStack.itemMeta?.getPersistentData(enchant)

    @EventHandler
    fun onAnvilEnchant(e: PrepareAnvilEvent) {
        if (!empireEnchant.generic.anvil) return
        if (e.inventory.firstItem?.isWhitelisted(materialWhitelist) != true)
            return
        val power = parseAnvilEnchant(e, enchant, enchantKey) ?: return
        e.inventory.repairCost += empireEnchant.generic.expPerLevel * power
    }

    @EventHandler
    fun onBookEnchant(e: EnchantItemEvent) {
        if (!empireEnchant.generic.enchantingTable) return
        if (!e.item.isWhitelisted(materialWhitelist)) return
        val i = e.whichButton()
        val chance = empireEnchant.generic.chances.getOrNull(i)?:0.0
        if (!calcChance(chance)) return
        val level = EmpireEnchantApi.getEnchantementLevel(i, empireEnchant.generic.maxLevel)
        e.item.setEmpireEnchantment(enchant, level, enchantKey)
    }

    override fun onDisable() {
        EnchantItemEvent.getHandlerList().unregister(this)
        PrepareAnvilEvent.getHandlerList().unregister(this)
    }
}
