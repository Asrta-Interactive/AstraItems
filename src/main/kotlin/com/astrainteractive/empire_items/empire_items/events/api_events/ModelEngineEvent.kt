package com.astrainteractive.empire_items.empire_items.events.api_events

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.model_engine.MobEvent
import com.astrainteractive.empire_items.api.model_engine.ModelEngineApi
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent

class ModelEngineEvent {
    val onMobSpawn = DSLEvent.event(EntitySpawnEvent::class.java) { e ->
        ModelEngineApi.requestMobSpawn(e.location, e.entity)
        ModelEngineApi.triggerEvent(MobEvent.ON_SPAWN, e.entity)
    }
    val onEntityTarget = DSLEvent.event(EntityTargetEvent::class.java) { e ->
        e.isCancelled = ModelEngineApi.shouldTargetAt(e.entity, e.target) ?: return@event
    }
    val onDeath = DSLEvent.event(EntityDeathEvent::class.java) { e ->
        ModelEngineApi.removeEntity(e.entity)
        ModelEngineApi.triggerEvent(MobEvent.ON_KILLED, e.entity)
    }
    val onRemovedFromWorld = DSLEvent.event(EntityRemoveFromWorldEvent::class.java) { e ->
        ModelEngineApi.removeEntity(e.entity)
    }
    val entityAttackEvent = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        ModelEngineApi.getCustomEntityInfo(e.entity)?:return@event
        e.isCancelled = ModelEngineApi.onAttack(e.damager, e.entity, e.damage)
        if (!e.isCancelled)
            ModelEngineApi.triggerEvent(MobEvent.ON_DAMAGE, e.damager, e.entity)
    }
    val entityDamagedEvent = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        ModelEngineApi.getCustomEntityInfo(e.entity)?:return@event
        if (e.isCancelled) return@event
        ModelEngineApi.triggerEvent(MobEvent.ON_DAMAGED, e.entity, e.damager)
        ModelEngineApi.onEntityDamaged(e.entity)
    }

}