package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getPersistentData
import org.bukkit.entity.EntityType
import org.bukkit.entity.Slime
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEntityEvent

class SlimeCatchEvent: EventListener {

    @EventHandler
    private fun onSlimeClick(e:PlayerInteractEntityEvent){
        val player = e.player
        val entity = e.rightClicked
        if (entity.type!=EntityType.SLIME)
            return
        val slime = entity as Slime
        if (slime.size>1)
            return
        val newItemId = player.inventory.itemInMainHand.itemMeta.getPersistentData(BukkitConstants.SLIME_CATCHER)?:return
        val newItem = newItemId.toAstraItemOrItem()?:return
        slime.remove()
        player.inventory.itemInMainHand.amount-=1
        player.inventory.addItem(newItem)


    }

    override fun onDisable() {
        PlayerInteractEntityEvent.getHandlerList().unregister(this)
    }
}