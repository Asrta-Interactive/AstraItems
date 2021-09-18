package com.makeevrserg.empireprojekt.empire_items.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empire_items.api.MushroomBlockApi
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener

import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent

class MushroomBlockPlaceEvent:IEmpireListener {






    public override fun onDisable(){
        BlockPlaceEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun blockPlace(e:BlockPlaceEvent){
        val player = e.player
        val block = e.block
        val id = ItemsAPI.getEmpireID(player.inventory.itemInMainHand)?:return
        val empireBlock = ItemsAPI.getEmpireBlockInfoById(id)?:return
        val empireFacings = MushroomBlockApi.getFacingByData(empireBlock.data)?:return
        block.type = MushroomBlockApi.getMaterialByData(empireBlock.data)
        val facing = block.blockData as MultipleFacing
        for (f in empireFacings.facing)
            facing.setFace(BlockFace.valueOf(f.key.uppercase()),f.value)
        e.block.blockData = facing

    }

}