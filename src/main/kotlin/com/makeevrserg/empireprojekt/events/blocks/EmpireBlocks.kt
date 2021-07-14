package com.makeevrserg.empireprojekt.events.blocks

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.makeevrserg.empireprojekt.util.ResourcePackNew
import net.minecraft.core.BlockPosition
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.world.ChunkEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkPopulateEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitScheduler
import java.lang.Math.abs
import kotlin.random.Random


class EmpireBlocks() : Listener {


    @EventHandler
    public fun onMushroomPhysics(e: BlockPhysicsEvent) {
        val block = e.block
        if (block.blockData !is MultipleFacing)
            return
        e.isCancelled = true
    }

    @EventHandler
    public fun blockPlaceEvent(e: BlockPlaceEvent) {
        val item = e.itemInHand
        val id = EmpireUtils.getEmpireID(item) ?: return
        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id] ?: return

        e.player.sendBlockBreakPacket(e.block, 0)
        val block = e.block
        block.location.world!!.playSound(block.location, empireBlock.place_sound ?: "", 1.0f, 1.0f)
        val state = ResourcePackNew.generateStateByData(empireBlock.data ?: return)
        block.type = Material.BROWN_MUSHROOM_BLOCK
        val data = block.blockData as MultipleFacing
        block.blockData = EmpireUtils.setBlockFace(data, state)
    }

    @EventHandler
    public fun blockBreakEvent(e: BlockBreakEvent) {
        val block = e.block
        val id = getCustomBlock(block)
        val item = EmpirePlugin.empireItems.empireItems[id] ?: return
        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id] ?: return

        block.location.world!!.playSound(block.location, empireBlock.break_sound ?: "", 1.0f, 1.0f)
        block.location.world!!.dropItem(block.location, item)

    }

    private fun getWhen(set: Set<BlockFace>): ResourcePackNew.When {
        return ResourcePackNew.When(
            set.contains((BlockFace.DOWN)) ?: false,
            set.contains((BlockFace.EAST)) ?: false,
            set.contains((BlockFace.NORTH)) ?: false,
            set.contains((BlockFace.SOUTH)) ?: false,
            set.contains((BlockFace.UP)) ?: false,
            set.contains((BlockFace.WEST)) ?: false,
        )
    }


    fun getCustomBlock(block: Block): String? {

        if (block.blockData !is MultipleFacing)
            return null
        val data = block.blockData as MultipleFacing
        val blockIntData = ResourcePackNew.generateDataByState(getWhen(data.faces))
        val id = EmpirePlugin.empireItems._empireBlocksByData[blockIntData] ?: return null
        return id
    }

    val blockDamageMap = mutableMapOf<Player, Long>()

    @EventHandler
    fun BlockDamageEvent(e: BlockDamageEvent) {
        if (getCustomBlock(e.block) == null) {
            blockDamageMap.remove(e.player)
            return
        }
        e.player.sendBlockBreakPacket(e.block, 0)
        blockDamageMap[e.player] = System.currentTimeMillis()
    }

    @EventHandler
    fun BlockBreakEvent(e: BlockBreakEvent) {
        blockDamageMap.remove(e.player)
        e.player.sendBlockBreakPacket(e.block, 0)

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
        val id = getCustomBlock(block)
        val item = EmpirePlugin.empireItems.empireItems[id] ?: return
        val empireBlock = EmpirePlugin.empireItems._empireBlocks[id] ?: return
        val time = System.currentTimeMillis().minus(blockDamageMap[e.player] ?: return) / 10.0


        player.sendBlockBreakPacket(block, (time / empireBlock.hardness.toDouble() * 9).toInt())


        if (time > empireBlock.hardness) {
            player.breakBlock(block)
            player.sendBlockBreakPacket(block, 0)
        }

        player.addPotionEffect(
            PotionEffect(PotionEffectType.SLOW_DIGGING, 5, 200, false, false, false)
        )

    }


    var blockGeneration: BlockGeneration? = null

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
        if (EmpirePlugin.config.generateBlocks)
            blockGeneration = BlockGeneration()

    }

    public fun onDisable() {
        BlockPlaceEvent.getHandlerList().unregister(this)
        BlockPhysicsEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)
        BlockDamageEvent.getHandlerList().unregister(this)
        PlayerAnimationEvent.getHandlerList().unregister(this)
        Bukkit.getScheduler().cancelTasks(EmpirePlugin.instance)
        if (blockGeneration != null)
            blockGeneration!!.onDisable()

    }
}