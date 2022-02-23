package com.astrainteractive.empire_items.empire_items.events.blocks

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.api.items.data.ItemApi
import net.minecraft.core.BlockPosition
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

class BlockHardnessEvent : EventListener {
    public override fun onDisable() {
        BlockBreakEvent.getHandlerList().unregister(this)
        PlayerAnimationEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
        BlockDamageEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
    }
    data class BreakTimeID(
        val time: Long = System.currentTimeMillis(),
        val id: Int = Random.nextInt(6000)
    )

    private val blockDamageMap = mutableMapOf<String, BreakTimeID>()

    @EventHandler
    fun playerQuitEvent(e: PlayerQuitEvent) {
        blockDamageMap.remove(e.player.name)
    }

    @EventHandler
    fun blockDamageEvent(e: BlockDamageEvent) {
        BlockParser.getBlockData(e.block) ?: return
        blockDamageMap[e.player.name] = BreakTimeID()
        e.player.sendBlockBreakPacket(e.block, 100)
    }

    @EventHandler
    fun blockBreakEvent(e: BlockBreakEvent) {
        e.player.sendBlockBreakPacket(e.block, 100)
        blockDamageMap.remove(e.player.name)
    }

    private fun Player.sendBlockBreakPacket(block: Block, breakProgress: Int) {
        val packet = PacketPlayOutBlockBreakAnimation(
            blockDamageMap[player?.name]?.id ?: player?.entityId ?: return,
            BlockPosition(block.x, block.y, block.z),
            breakProgress
        )
        (this as CraftPlayer).handle.b.a(packet)
    }


    @EventHandler
    fun playerBreakingEvent(e: PlayerAnimationEvent) {
        val block = e.player.getTargetBlock(null, 100)
        val player = e.player
        val data = BlockParser.getBlockData(block) ?: return
        val itemInfo = ItemApi.getBlockInfoByData(data)?: return
        val empireBlock = itemInfo.block?:return
        empireBlock.hardness?:return
        val digMultiplier = e.player.inventory.itemInMainHand.enchantments[Enchantment.DIG_SPEED] ?: 1
        val time = (System.currentTimeMillis().minus(blockDamageMap[e.player.name]?.time ?: return) / 10.0) * digMultiplier
        player.sendBlockBreakPacket(block, (time / empireBlock.hardness.toDouble() * 9).toInt())
        if (time > empireBlock.hardness) {
            player.breakBlock(block)
            player.sendBlockBreakPacket(block, 100)
        }
        player.addPotionEffect(
            PotionEffect(PotionEffectType.SLOW_DIGGING, 5, 200, false, false, false)
        )
    }

}