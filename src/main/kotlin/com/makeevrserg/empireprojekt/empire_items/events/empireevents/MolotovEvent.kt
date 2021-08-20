package com.makeevrserg.empireprojekt.empire_items.events.empireevents

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.regions.RegionQuery
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.persistence.PersistentDataType

class MolotovEvent : IEmpireListener {



    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        if (e.entity.shooter !is Player) return
        val player = e.entity.shooter as Player
        val itemStack = player.inventory.itemInMainHand
        val meta = itemStack.itemMeta ?: return
        val molotovPower =
            meta.persistentDataContainer.get(BetterConstants.MOLOTOV.value, PersistentDataType.DOUBLE)
                ?: return
        println("Player ${player.name} threw molotov at blockLocation=${e.hitBlock?.location} playerLocation=${player.location}")
        Igniter(instance, e.hitBlock ?: return, molotovPower.toInt(), player)
    }

    companion object {
        fun allowFire(plugin: EmpirePlugin, location: Location): Boolean {
            if (plugin.server.pluginManager.getPlugin("WorldGuard") != null) {
                val query: RegionQuery = WorldGuard.getInstance().platform.regionContainer.createQuery()
                val loc: com.sk89q.worldedit.util.Location = BukkitAdapter.adapt(location)
                return (query.testState(loc, null, Flags.FIRE_SPREAD))
            }
            return true
        }
    }

    class Igniter(val plugin: EmpirePlugin, block: Block, radius: Int, player: Player) {
        private val listLocations: MutableList<Location> = mutableListOf()

        init {
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

        private fun setFire(block: Block, radius: Int, player: Player) {
            if (!allowFire(plugin, block.location))
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


    override fun onDisable() {
        ProjectileHitEvent.getHandlerList().unregister(this)
    }
}
