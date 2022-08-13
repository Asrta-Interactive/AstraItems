package com.astrainteractive.empire_items.empire_items.events.api_events

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.empire_items.events.api_events.model_engine.ModelEngineApi
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent

class NewModelEngineEvent {
    val onMobSpawn = DSLEvent.event(EntitySpawnEvent::class.java) { e ->
        ModelEngineApi.requestMobSpawn(e.location, e.entity)
    }
    val onEntityTarget = DSLEvent.event(EntityTargetEvent::class.java) { e ->
        e.isCancelled = ModelEngineApi.shouldTargetAt(e.entity, e.target) ?: return@event
    }
    val onDeath = DSLEvent.event(EntityDeathEvent::class.java) { e ->
        ModelEngineApi.removeEntity(e.entity)
    }
    val onRemovedFromWorld = DSLEvent.event(EntityRemoveFromWorldEvent::class.java) { e ->
        ModelEngineApi.removeEntity(e.entity)
    }
    val entityAttackEvent = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        e.isCancelled = ModelEngineApi.onAttack(e.damager, e.entity, e.damage)
    }
}