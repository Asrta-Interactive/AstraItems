package com.astrainteractive.empire_items.empire_items.events.decoration

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.items.DecorationBlockAPI
import com.astrainteractive.empire_items.api.items.data.ItemApi.getAstraID
import com.astrainteractive.empire_items.empire_items.util.playSound
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class DecorationEvent : EventListener {

    @EventHandler
    fun playerInteractEntityEvent(e: HangingPlaceEvent) {
        if (e.entity !is ItemFrame)
            return
        val itemFrame: ItemFrame = e.entity as ItemFrame
        val id = e.itemStack?.clone()?.getAstraID() ?: return
        DecorationBlockAPI.placeBlock(id, itemFrame, e.player?.location ?: return, e.blockFace)
    }

    @EventHandler
    fun decorationInteractEvent(e: PlayerInteractEvent) {
        val block = e.clickedBlock ?: return
        if (block.type != Material.BARRIER)
            return
        if (e.action == Action.LEFT_CLICK_BLOCK && e.hand == EquipmentSlot.HAND)
            DecorationBlockAPI.breakItem(block.location)
    }


    override fun onDisable() {
    }
}