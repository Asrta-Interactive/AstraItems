package com.astrainteractive.empire_items.empire_items.events.genericevents

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.EmpireAPI
import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.items.data.ItemApi.getAstraID
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.api.utils.setPersistentDataType

import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ExperienceRepairEvent{


    val repairEvent = DSLEvent.event(PlayerItemMendEvent::class.java)  { e ->
        changeDurability(e.item, e.repairAmount)
    }

    val durabilityEvent = DSLEvent.event(PlayerItemDamageEvent::class.java)  { e ->
        if (ItemApi.getItemInfo(e.item?.getAstraID())?.gun != null) {
            e.isCancelled = true
            return@event
        }
        changeDurability(e.item, -e.damage)
    }


    val anvilEvent = DSLEvent.event(PrepareAnvilEvent::class.java)  { e ->
        val itemStack: ItemStack = e.result ?: return@event
        val itemMeta: ItemMeta = itemStack.itemMeta ?: return@event


        val maxCustomDurability: Int = itemMeta.getPersistentData(BukkitConstants.MAX_CUSTOM_DURABILITY) ?: return@event

        val damage: Short = itemStack.durability
        val empireDurability = maxCustomDurability - damage * maxCustomDurability / itemStack.type.maxDurability
        itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY, empireDurability)
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

        itemMeta.setPersistentDataType(BukkitConstants.EMPIRE_DURABILITY, empireDurability)
        itemStack.itemMeta = itemMeta

        if (maxCustomDurability == 0)
            maxCustomDurability = itemStack.type.maxDurability.toInt()
        val d: Int = itemStack.type.maxDurability -
                itemStack.type.maxDurability * empireDurability / maxCustomDurability
        itemStack.durability = d.toShort()

    }
}