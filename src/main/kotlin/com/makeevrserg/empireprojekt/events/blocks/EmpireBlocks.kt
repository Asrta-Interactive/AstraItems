//package com.makeevrserg.empireprojekt.events.blocks
//
//import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
//import org.bukkit.Instrument
//import org.bukkit.Material
//import org.bukkit.block.Block
//import org.bukkit.block.BlockFace
//import org.bukkit.block.BlockState
//import org.bukkit.block.data.type.NoteBlock
//import org.bukkit.event.EventHandler
//import org.bukkit.event.Listener
//import org.bukkit.event.block.*
//import java.util.*
//
//class EmpireBlocks() : Listener {
//
//
//    private fun setNoteBlock(block: Block): Boolean {
//        if (block.blockData !is NoteBlock)
//            return false
//        val noteBlock = block.blockData as NoteBlock
//        noteBlock.instrument = Instrument.PIANO
//        block.blockData = noteBlock
//        block.state.update(true)
//        return true
//    }
//
//
//
//    @EventHandler
//    fun notePlayEvent(e:NotePlayEvent){
//        val block = e.block.getRelative(BlockFace.DOWN)
//        val material = block.type
//        when (block.type){
//            Material.ACACIA_WOOD,Material.BIRCH_WOOD->{
//                block.location.world.playN
//            }
//            else -> {
//                return
//            }
//        }
//    }
//    @EventHandler
//    fun blockPhysicsEvent(e:BlockPhysicsEvent){
//        setNoteBlock(e.block)
//    }
//
//    @EventHandler
//    public fun blockPlaceEvent(e: BlockPlaceEvent) {
////        val blockPlaced = e.blockPlaced
////        setNoteBlock(blockPlaced)
////        val upperBlock = blockPlaced.getRelative(BlockFace.UP)
////        setNoteBlock(e.block)
////        setNoteBlock(upperBlock)
//    }
//
//
//    init {
//        plugin.server.pluginManager.registerEvents(this, plugin)
//    }
//
//    public fun onDisable() {
//        BlockPlaceEvent.getHandlerList().unregister(this)
//        BlockPhysicsEvent.getHandlerList().unregister(this)
//        NotePlayEvent.getHandlerList().unregister(this)
//    }
//}