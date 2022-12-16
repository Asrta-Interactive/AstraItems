package com.astrainteractive.empire_items.events.empireevents

import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import com.astrainteractive.empire_itemss.api.utils.explode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import ru.astrainteractive.astralibs.utils.AstraLibsExtensions.getPersistentData

class GrenadeEvent{


    val onProjectileHit = DSLEvent.event(ProjectileHitEvent::class.java)  { e ->
        if (e.entity.shooter !is Player) return@event
        val player = e.entity.shooter as Player

        val itemStack = player.inventory.itemInMainHand
        val meta = itemStack.itemMeta ?: return@event


        val explosionPower = meta.getPersistentData(BukkitConstants.GRENADE_EXPLOSION_POWER) ?: return@event
        Logger.log("Player ${player.name} threw grenade at blockLocation=${e.hitBlock?.location} playerLocation=${player.location}","Grenade")
        e.entity.location.explode(explosionPower)
        e.entity.world.spawnParticle(Particle.SMOKE_LARGE, e.entity.location, 300, 0.0, 0.0, 0.0, 0.2)
    }


    companion object {
        fun generateExplosion(location: Location, power: Double) {
            location.world?.createExplosion(location, power.toFloat()) ?: return
        }
    }
}
