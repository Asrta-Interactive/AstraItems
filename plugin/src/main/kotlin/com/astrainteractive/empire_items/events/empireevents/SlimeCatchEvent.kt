package com.astrainteractive.empire_items.events.empireevents

import com.astrainteractive.empire_items.util.EmpireItemsAPIExt.toAstraItemOrItem
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import org.bukkit.entity.EntityType
import org.bukkit.entity.Slime
import org.bukkit.event.player.PlayerInteractEntityEvent
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.AstraLibsExtensions.getPersistentData

class SlimeCatchEvent {

    val onSlimeClick = DSLEvent.event<PlayerInteractEntityEvent>  { e ->
        val player = e.player
        val entity = e.rightClicked
        if (entity.type!=EntityType.SLIME)
            return@event
        val slime = entity as Slime
        if (slime.size>1)
            return@event
        val newItemId = player.inventory.itemInMainHand.itemMeta.getPersistentData(BukkitConstants.SLIME_CATCHER)?:return@event
        val newItem = newItemId.toAstraItemOrItem()?:return@event
        slime.remove()
        player.inventory.itemInMainHand.amount-=1
        player.inventory.addItem(newItem)


    }
}