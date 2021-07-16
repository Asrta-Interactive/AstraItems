package com.makeevrserg.empireprojekt.ESSENTIALS

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

class AutoBlockChange : Listener {


    @EventHandler
    fun onBlockPlaced(e: BlockPlaceEvent) {


        val player = e.player
        val blockPlaced = e.blockPlaced
        val itemInHand = player.inventory.itemInMainHand
        if (EmpireUtils.getEmpireID(itemInHand)!=null)
            return
        if (blockPlaced.type!=itemInHand.type)
            return

        val material = itemInHand.type
        if (itemInHand.amount > 1)
            return
        else
            itemInHand.amount -= 1
        if (!player.inventory.contains(material))
            return

        val itemIndex = player.inventory.first(material)
        if (itemIndex == -1) return


        val itemStack = player.inventory.getItem(itemIndex)?.clone() ?: return

        player.inventory.setItem(itemIndex, null)

        if (player.inventory.itemInMainHand.type == Material.AIR)
            player.inventory.setItemInMainHand(itemStack)
        else if (player.inventory.itemInOffHand.type == Material.AIR)
            player.inventory.setItemInOffHand(itemStack)

    }

    @EventHandler
    fun onCropInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK)
            return
        val block = e.clickedBlock?:return
        if (block.blockData !is Ageable)
            return

        val blockAge = block.blockData as Ageable

        if (blockAge.age!=blockAge.maximumAge)
            return

        val material = block.type
        val location = block.location
        block.breakNaturally()

        location.world?.getBlockAt(location)?.type = material

    }

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    fun onDisable() {
        BlockPlaceEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

}