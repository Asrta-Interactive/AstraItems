package com.makeevrserg.empireprojekt.essentials

import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

class AutoBlockChangeEvent : IEmpireListener {


    @EventHandler
    fun onBlockPlaced(e: BlockPlaceEvent) {


        val player = e.player
        val blockPlaced = e.blockPlaced
        val itemInHand = player.inventory.itemInMainHand
        if (ItemsAPI.getEmpireID(itemInHand) != null)
            return
        if (blockPlaced.type != itemInHand.type)
            return

        val material = itemInHand.type

        if (itemInHand.amount > 1)
            return

        itemInHand.amount -= 1

        if (!player.inventory.contains(material))
            return
        if (player.inventory.itemInMainHand.type != Material.AIR)
            return

        val itemIndexMap = player.inventory.all(material)
        itemIndexMap.remove(player.inventory.heldItemSlot)
        val itemIndex = itemIndexMap.keys.elementAt(0) ?: return
        val itemStack = player.inventory.getItem(itemIndex)?.clone() ?: return
        player.inventory.setItem(itemIndex, null)
        player.inventory.setItemInMainHand(itemStack)


    }

    @EventHandler
    fun onCropInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return
        val block = e.clickedBlock ?: return
        if (block.blockData !is Ageable)
            return

        val blockAge = block.blockData as Ageable

        if (blockAge.age != blockAge.maximumAge)
            return

        val material = block.type
        val location = block.location
        block.breakNaturally()

        location.world?.getBlockAt(location)?.type = material

    }






    override fun onDisable() {
        BlockPlaceEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

}