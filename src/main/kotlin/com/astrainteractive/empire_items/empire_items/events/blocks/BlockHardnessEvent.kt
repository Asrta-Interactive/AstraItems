package com.astrainteractive.empire_items.empire_items.events.blocks

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.util.CleanerTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.minecraft.core.BlockPosition
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random


class BlockHardnessEvent {
    data class BreakTimeID(
        val time: Long = System.currentTimeMillis(),
        val id: Int = Random.nextInt(Int.MAX_VALUE),
    )

    val cleaner = CleanerTask(50000) {
        blockDamageMap.clear()
    }

    private val blockDamageMap = mutableMapOf<String, BreakTimeID>()

    val playerQuitEvent = DSLEvent.event(PlayerQuitEvent::class.java) { e ->
        blockDamageMap.remove(e.player.name)
    }

    val blockDamageEvent = DSLEvent.event(BlockDamageEvent::class.java) { e ->
        BlockParser.getBlockData(e.block) ?: return@event
        blockDamageMap[e.player.name] = BreakTimeID()
        e.player.sendBlockBreakPacket(e.block, 100)
    }

    val blockBreakEvent = DSLEvent.event(BlockBreakEvent::class.java) { e ->
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


    val playerBreakingEvent = DSLEvent.event(PlayerAnimationEvent::class.java) { e ->
        val block = e.player.getTargetBlock(null, 10)
        val player = e.player
        val data = BlockParser.getBlockData(block) ?: return@event
        val itemInfo = EmpireItemsAPI.itemYamlFilesByID.values.firstOrNull { it.block?.data == data } ?: return@event
        val empireBlock = itemInfo.block ?: return@event
        empireBlock.hardness ?: return@event
        val digMultiplier = e.player.inventory.itemInMainHand.enchantments[Enchantment.DIG_SPEED] ?: 1
        val time = (System.currentTimeMillis()
            .minus(blockDamageMap[e.player.name]?.time ?: return@event) / 10.0) * digMultiplier
        player.sendBlockBreakPacket(block, (time / empireBlock.hardness!!.toDouble() * 9).toInt())
        if (time > empireBlock.hardness!!) {
            player.breakBlock(block)
            player.sendBlockBreakPacket(block, 100)
        }
        player.addPotionEffect(
            PotionEffect(PotionEffectType.SLOW_DIGGING, 5, 200, false, false, false)
        )
    }

}