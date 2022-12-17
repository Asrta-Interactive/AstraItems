package com.astrainteractive.empire_items.events.api_events

import com.astrainteractive.empire_items.di.bossBarControllerModule
import com.astrainteractive.empire_items.di.empireModelEngineApiModule
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.models_ext.play
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent
import ru.astrainteractive.astralibs.di.getValue

class ModelEngineEvent {
    private val empireModelEngineAPI by empireModelEngineApiModule
    private val bossBarController by bossBarControllerModule
    val onMobSpawn = DSLEvent.event(EntitySpawnEvent::class.java) { e ->
        empireModelEngineAPI.processSpawnedMob(e.location, e.entity)
    }

    val onEntityTarget = DSLEvent.event(EntityTargetEvent::class.java) { e ->
        empireModelEngineAPI.onEntityTarget(e)
    }
    val onDeath = DSLEvent.event(EntityDeathEvent::class.java) { e ->
        val entity = empireModelEngineAPI.getEmpireEntity(e.entity) ?: return@event
        entity.ymlMob.events["onDeath"]?.playSound?.play(e.entity.location)
        bossBarController.onEntityDead(e.entity)
    }

    val entityAttackEvent = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        empireModelEngineAPI.performAttack(e)
    }
    val onEmpireEntityDamaged = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        val entity = empireModelEngineAPI.getEmpireEntity(e.entity) ?: return@event
        entity.ymlMob.events["onDamaged"]?.playSound?.play(e.entity.location)
        bossBarController.onEntityDamaged(e.entity)
    }

}