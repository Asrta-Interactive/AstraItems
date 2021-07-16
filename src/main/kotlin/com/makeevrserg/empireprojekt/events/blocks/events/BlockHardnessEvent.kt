package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import net.minecraft.core.BlockPosition
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class BlockHardnessEvent:Listener {
    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }
    public fun onDisable(){
        BlockBreakEvent.getHandlerList().unregister(this)
        PlayerAnimationEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
        BlockDamageEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
    }



    val blockDamageMap = mutableMapOf<Player, Long>()

    @EventHandler
    fun playerQuitEvent(e:PlayerQuitEvent){
        blockDamageMap.remove(e.player)
    }
    @EventHandler
    fun BlockDamageEvent(e: BlockDamageEvent) {
        MushroomBlockApi.getBlockData(e.block)?:return
        e.player.sendBlockBreakPacket(e.block, 10)
        blockDamageMap[e.player] = System.currentTimeMillis()
    }
    @EventHandler
    fun BlockBreakEvent(e: BlockBreakEvent) {
        blockDamageMap.remove(e.player)
        e.player.sendBlockBreakPacket(e.block, 10)
    }

    private fun Player.sendBlockBreakPacket(block: Block, breakProgress: Int) {
        val packet = PacketPlayOutBlockBreakAnimation(
            10,
            BlockPosition(block.x, block.y, block.z),
            breakProgress
        )
        (this as CraftPlayer).handle.b.sendPacket(packet)
    }

    @EventHandler
    fun playerBreakingEvent(e: PlayerAnimationEvent) {
        val block = e.player.getTargetBlock(null, 100)
        val player = e.player
        val data = MushroomBlockApi.getBlockData(block) ?: return
        val id = EmpirePlugin.empireItems._empireBlocksByData[data] ?: return
        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id] ?: return
        val time = System.currentTimeMillis().minus(blockDamageMap[e.player] ?: return) / 10.0
        player.sendBlockBreakPacket(block, (time / empireBlock.hardness.toDouble() * 9).toInt())
        if (time > empireBlock.hardness) {
            player.breakBlock(block)
            player.sendBlockBreakPacket(block, 10)
        }

        player.addPotionEffect(
            PotionEffect(PotionEffectType.SLOW_DIGGING, 5, 200, false, false, false)
        )

    }

}