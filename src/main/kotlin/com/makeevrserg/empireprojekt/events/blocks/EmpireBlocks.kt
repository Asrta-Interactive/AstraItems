package com.makeevrserg.empireprojekt.events.blocks

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import com.makeevrserg.empireprojekt.EmpirePlugin
import net.minecraft.core.BlockPosition
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import kotlin.math.min


class EmpireBlocks() : Listener {


//    private fun setNoteBlock(block: Block): Boolean {
//        if (block.blockData !is NoteBlock)
//            return false
//        val noteBlock = block.blockData as NoteBlock
//        noteBlock.instrument = Instrument.PIANO
//        block.blockData = noteBlock
//        block.state.update(true)
//        return true
//    }


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

    //    @EventHandler
//    public fun blockPlaceEvent(e: BlockPlaceEvent) {
////        val blockPlaced = e.blockPlaced
////        setNoteBlock(blockPlaced)
////        val upperBlock = blockPlaced.getRelative(BlockFace.UP)
////        setNoteBlock(e.block)
////        setNoteBlock(upperBlock)
//    }
    @EventHandler
    public fun BlockDamageEvent(e: BlockDamageEvent) {
        println("DamageEvent")
//        Bukkit.getScheduler().runTaskTimerAsynchronously(EmpirePlugin.instance,
//            Runnable {
//                println("Timer")
//                val blockPlaced = e.block
//                val player = e.player
//                val minePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
//                minePacket.blockPositionModifier.write(0, com.comphenix.protocol.wrappers.BlockPosition(blockPlaced.x,blockPlaced.y,blockPlaced.z))
//                minePacket.integers.write(0, player.entityId)
//                minePacket.integers.write(1, 0)
//                protocolManager.sendServerPacket(player, minePacket)
//                     },0,0)


    }

//    private lateinit var protocolManager: ProtocolManager
//    private lateinit var packetListener: PacketListener
//    private fun initPackerListener() {
//        packetListener = object : PacketAdapter(
//            EmpirePlugin.instance,
//            ListenerPriority.HIGHEST,
//            PacketType.Play.Server.BLOCK_BREAK,
//            PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
//            PacketType.Play.Server.ANIMATION
//        ) {
//            override fun onPacketReceiving(event: PacketEvent) {
//                val packet = event.packet
//                println("onPacketReceiving: " + event.packet.type.name());
//
//            }
//
//            override fun onPacketSending(event: PacketEvent) {
//                println("onPacketSending: " + event.packet.type.name());
//                val packet = event.packet
//                val player = event.player
//
//                for (i in 0 until packet.integers.size())
//                    println(packet.integers.read(i))
////                val minePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
////                minePacket.blockPositionModifier.write(0, packet.blockPositionModifier.values.elementAt(0))
////                minePacket.integers.write(0,player.entityId)
////                minePacket.integers.write(1,0)
////                protocolManager.sendServerPacket(player, minePacket)
//
//
//            }
//        }
//        protocolManager.addPacketListener(packetListener)
//    }

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)

        EmpirePlugin.instance.server.pluginManager.getPlugin("protocollib")?.let {
//            protocolManager = ProtocolLibrary.getProtocolManager()
//            initPackerListener()
        }


    }

    public fun onDisable() {
//        protocolManager.removePacketListener(packetListener)
        BlockDamageEvent.getHandlerList().unregister(this)

    }
}