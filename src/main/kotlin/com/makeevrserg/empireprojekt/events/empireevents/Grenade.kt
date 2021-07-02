package com.makeevrserg.empireprojekt.events.empireevents

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.regions.RegionQuery
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.persistence.PersistentDataType

class Grenade : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        if (e.entity.shooter !is Player) return
        val player = e.entity.shooter as Player
        val itemStack = player.inventory.itemInMainHand
        val meta = itemStack.itemMeta ?: return
        val explosionPower =
            meta.persistentDataContainer.get(plugin.empireConstants.GRENADE_EXPLOSION_POWER, PersistentDataType.DOUBLE)
                ?: return
        if (denyExplosion(plugin, e.entity.location)) {
            return
        }
        generateExplosion(e.entity.location, explosionPower)
        e.entity.world.spawnParticle(Particle.SMOKE_LARGE, e.entity.location, 300, 0.0, 0.0, 0.0, 0.2)
    }

    fun onDisable() {
        ProjectileHitEvent.getHandlerList().unregister(this)
    }

    companion object {
        fun denyExplosion(plugin: EmpirePlugin, location: Location): Boolean {
            if (plugin.server.pluginManager.getPlugin("WorldGuard") != null) {
                val query: RegionQuery = WorldGuard.getInstance().platform.regionContainer.createQuery()
                val loc: com.sk89q.worldedit.util.Location = BukkitAdapter.adapt(location)
                return !query.testState(loc, null, Flags.OTHER_EXPLOSION)

            }
            return false
        }

        fun generateExplosion(location: Location, power: Double) {

            location.world?.createExplosion(location,power.toFloat())?:return
        }
    }
}
