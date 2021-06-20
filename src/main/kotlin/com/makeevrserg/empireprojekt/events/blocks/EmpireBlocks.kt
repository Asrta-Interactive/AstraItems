package com.makeevrserg.empireprojekt.events.blocks

import com.destroystokyo.paper.profile.PlayerProfile
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*

class EmpireBlocks() : Listener {


    @EventHandler
    public fun noteBlockChangeEvent(e: NotePlayEvent) {
        println("NotePlayEvent")
    }


    private fun changeBlockData(blockData:BlockData): NoteBlock? {
        if (blockData !is NoteBlock)
            return null
        val data = blockData as NoteBlock
        data.instrument = Instrument.PIANO
        return data
    }
    private fun replaceNoteBlock(block: Block): Boolean {
        val upperBlock = block.getRelative(BlockFace.UP)
        upperBlock.blockData = changeBlockData(upperBlock.blockData)?:upperBlock.blockData
        block.blockData = changeBlockData(block.blockData)?:block.blockData
        if (upperBlock.blockData is NoteBlock || block.blockData is NoteBlock)
            return true
        return false


    }
    @EventHandler
    public fun blockPlaceEvent(e: BlockPlaceEvent) {
        val blockPlaced = e.blockPlaced
        if (replaceNoteBlock(blockPlaced)){
            e.itemInHand.amount-=1
            e.isCancelled = true
            blockPlaced.location.block.type = blockPlaced.type


        }
        val server = plugin.server
        val world = e.block.location.world
    }


    init {
        //plugin.server.pluginManager.registerEvents(this, plugin)




    }

    public fun onDisable() {
//        BlockPlaceEvent.getHandlerList().unregister(this)
//
//        BlockRedstoneEvent.getHandlerList().unregister(this)
//        NotePlayEvent.getHandlerList().unregister(this)

    }
}