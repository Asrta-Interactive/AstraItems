package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class MushroomBlockPlaceEvent:Listener {





    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }
    public fun onDisable(){
        BlockPlaceEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun blockPlace(e:BlockPlaceEvent){
        val player = e.player
        val block = e.block
        val id = EmpireUtils.getEmpireID(player.inventory.itemInMainHand)
        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id]?:return
        val empireFacings = MushroomBlockApi.getFacingByData(empireBlock.data)?:return
        block.type = MushroomBlockApi.getMaterialByData(empireBlock.data)
        val facing = block.blockData as MultipleFacing
        for (f in empireFacings.facing)
            facing.setFace(BlockFace.valueOf(f.key.uppercase()),f.value)
        e.block.blockData = facing

    }

}