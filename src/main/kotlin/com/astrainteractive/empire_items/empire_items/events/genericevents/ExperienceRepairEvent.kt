package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empire_items.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.empire_items.api.utils.setPersistentDataType

import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ExperienceRepairEvent : IAstraListener {



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


        val maxCustomDurability: Int = itemMeta.getPersistentData(BukkitConstants.MAX_CUSTOM_DURABILITY) ?: return

        val damage: Short = itemStack.durability
        val empireDurability = maxCustomDurability - damage * maxCustomDurability / itemStack.type.maxDurability
        itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY,empireDurability)
        itemStack.itemMeta = itemMeta
        val d: Int = itemStack.type.maxDurability -
                itemStack.type.maxDurability * empireDurability / maxCustomDurability
        itemStack.durability = d.toShort()
    }

    private fun changeDurability(itemStack: ItemStack?, damage: Int) {
        itemStack ?: return
        val itemMeta: ItemMeta = itemStack.itemMeta ?: return

        var maxCustomDurability: Int = itemMeta.getPersistentData(BukkitConstants.MAX_CUSTOM_DURABILITY) ?: return

        var empireDurability: Int = itemMeta.getPersistentData(BukkitConstants.EMPIRE_DURABILITY) ?: return



        empireDurability += damage

        if (empireDurability <= 0) {
            itemStack.durability = 0
        }

        if (empireDurability >= maxCustomDurability) {
            empireDurability = maxCustomDurability
        }

        itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY,empireDurability)
        itemStack.itemMeta = itemMeta

        if (maxCustomDurability==0)
            maxCustomDurability = itemStack.type.maxDurability.toInt()
        val d: Int = itemStack.type.maxDurability -
                itemStack.type.maxDurability * empireDurability / maxCustomDurability
        itemStack.durability = d.toShort()

    }

    override fun onDisable() {
        PlayerItemMendEvent.getHandlerList().unregister(this)
        PlayerItemDamageEvent.getHandlerList().unregister(this)
        PrepareAnvilEvent.getHandlerList().unregister(this)
    }
}