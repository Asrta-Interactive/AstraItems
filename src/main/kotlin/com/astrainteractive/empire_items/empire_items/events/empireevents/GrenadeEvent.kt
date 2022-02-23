package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.empire_items.util.protection.KProtectionLib
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent

class GrenadeEvent : EventListener {


    override fun onDisable() {
        ProjectileHitEvent.getHandlerList().unregister(this)
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        if (e.entity.shooter !is Player) return
        val player = e.entity.shooter as Player

        val itemStack = player.inventory.itemInMainHand
        val meta = itemStack.itemMeta ?: return
        if (!KProtectionLib.canExplode(null,e.hitBlock?.location?:return))
            return

        val explosionPower = meta.getPersistentData(BukkitConstants.GRENADE_EXPLOSION_POWER) ?: return
        Logger.log("Player ${player.name} threw grenade at blockLocation=${e.hitBlock?.location} playerLocation=${player.location}","Grenade")
        generateExplosion(e.entity.location, explosionPower.toDouble())
        e.entity.world.spawnParticle(Particle.SMOKE_LARGE, e.entity.location, 300, 0.0, 0.0, 0.0, 0.2)
    }


    companion object {
        fun generateExplosion(location: Location, power: Double) {
            location.world?.createExplosion(location, power.toFloat()) ?: return
        }
    }
}