package com.astrainteractive.empire_items.empire_items.events.empireevents

import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

class MolotovEvent{


    val onProjectileHit = DSLEvent.event(ProjectileHitEvent::class.java)  { e ->
        if (e.entity.shooter !is Player) return@event
        val player = e.entity.shooter as Player
        val itemStack = player.inventory.itemInMainHand
        val meta = itemStack.itemMeta ?: return@event
        if (!KProtectionLib.canIgnite(null, e.hitBlock?.location ?: return@event))
            return@event
        val molotovPower =
            meta.persistentDataContainer.get(BukkitConstants.MOLOTOV.value, BukkitConstants.MOLOTOV.dataType)
                ?: return@event
        Logger.log(
            "Player ${player.name} threw molotov at blockLocation=${e.hitBlock?.location} playerLocation=${player.location}",
            "Molotov"
        )
        Igniter( e.hitBlock ?: return@event, molotovPower.toInt(), player)
    }

    class Igniter(block: Block, radius: Int, player: Player?,particle:Boolean=true) {
        private val listLocations: MutableList<Location> = mutableListOf()

        init {
            if (particle)
                block.location.world?.spawnParticle(Particle.SMOKE_LARGE, block.location, 300, 0.0, 0.0, 0.0, 0.2)
            setFire(block, radius, player)
        }


        private fun checkOnlyAir(block: Block): Boolean {
            val faces = listOf<BlockFace>(
                BlockFace.DOWN,
                BlockFace.UP,
                BlockFace.EAST,
                BlockFace.WEST,
                BlockFace.NORTH,
                BlockFace.SOUTH
            )
            for (face in faces)
                if (block.getRelative(face).type != Material.AIR)
                    return false
            return true
        }

        private fun checkOnlyBlock(block: Block): Boolean {
            val faces = listOf<BlockFace>(
                BlockFace.DOWN,
                BlockFace.UP,
                BlockFace.EAST,
                BlockFace.WEST,
                BlockFace.NORTH,
                BlockFace.SOUTH
            )
            for (face in faces)
                if (block.getRelative(face).type == Material.AIR)
                    return false
            return true
        }

        private fun setFire(block: Block, radius: Int, player: Player?) {
            if (block?.location?.let { KProtectionLib.canIgnite(null, it) } != true)
                return
            if (radius == 0)
                return
            if (checkOnlyAir(block))
                return
            if (checkOnlyBlock(block))
                return
            if (block.location in listLocations)
                return
            else
                listLocations.add(block.location)
            if (block.type == Material.AIR)
                block.type = Material.FIRE
            for (blockFace in BlockFace.values())
                setFire(block.getRelative(blockFace), radius - 1, player)


        }

    }
}
