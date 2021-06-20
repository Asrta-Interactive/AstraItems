package com.makeevrserg.empireprojekt.events.genericlisteners

import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class ExperienceRepairEvent : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun repairEvent(e: PlayerItemMendEvent) {
        changeDurability(e.item, e.repairAmount)
    }

    @EventHandler
    fun durabilityEvent(e: PlayerItemDamageEvent) {
        changeDurability(e.item, -e.damage)
    }

    @EventHandler
    fun anvilEvent(e: PrepareAnvilEvent) {
        val itemStack: ItemStack = e.result?:return
        val itemMeta: ItemMeta = itemStack.itemMeta ?: return

        val maxCustomDurability: Int = itemMeta.persistentDataContainer.get(
            plugin.empireConstants.MAX_CUSTOM_DURABILITY,
            PersistentDataType.INTEGER
        ) ?: return

        val damage: Short = itemStack.durability
        val empireDurability = maxCustomDurability - damage * maxCustomDurability / itemStack.type.maxDurability
        itemMeta.persistentDataContainer.set(
            plugin.empireConstants.EMPIRE_DURABILITY,
            PersistentDataType.INTEGER,
            empireDurability
        )
        itemStack.itemMeta = itemMeta
        val d: Int = itemStack.type.maxDurability -
                itemStack.type.maxDurability * empireDurability / maxCustomDurability
        itemStack.durability = d.toShort()
    }

    private fun changeDurability(itemStack: ItemStack?, damage: Int) {
        itemStack ?: return
        val itemMeta: ItemMeta = itemStack.itemMeta ?: return
        val maxCustomDurability: Int = itemMeta.persistentDataContainer.get(
            plugin.empireConstants.MAX_CUSTOM_DURABILITY,
            PersistentDataType.INTEGER
        ) ?: return

        var empireDurability: Int = itemMeta.persistentDataContainer.get(
            plugin.empireConstants.EMPIRE_DURABILITY,
            PersistentDataType.INTEGER
        ) ?: return


        empireDurability += damage

        if (empireDurability <= 0) {
            itemStack.durability = 0
        }

        if (empireDurability >= maxCustomDurability) {
            empireDurability = maxCustomDurability
        }

        itemMeta.persistentDataContainer.set(
            plugin.empireConstants.EMPIRE_DURABILITY,
            PersistentDataType.INTEGER,
            empireDurability
        )
        itemStack.itemMeta = itemMeta

        val d: Int = itemStack.type.maxDurability -
                itemStack.type.maxDurability * empireDurability / maxCustomDurability
        itemStack.durability = d.toShort()

    }

    fun onDisable() {
        PlayerItemMendEvent.getHandlerList().unregister(this)
        PlayerItemDamageEvent.getHandlerList().unregister(this)
        PrepareAnvilEvent.getHandlerList().unregister(this)
    }
}