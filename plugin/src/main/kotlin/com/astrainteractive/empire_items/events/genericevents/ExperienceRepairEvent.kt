package com.astrainteractive.empire_items.events.genericevents

import com.astrainteractive.empire_items.di.empireItemsApiModule
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.empireID
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants

import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemMendEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.AstraLibsExtensions.getPersistentData
import ru.astrainteractive.astralibs.utils.AstraLibsExtensions.setPersistentDataType

class ExperienceRepairEvent{
    private val empireItemsAPI by empireItemsApiModule

    val repairEvent = DSLEvent.event(PlayerItemMendEvent::class.java)  { e ->
        changeDurability(e.item, e.repairAmount)
    }

    val durabilityEvent = DSLEvent.event(PlayerItemDamageEvent::class.java)  { e ->
        if (empireItemsAPI.itemYamlFilesByID[e.item.empireID]?.gun != null) {
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