package com.astrainteractive.empire_items.events.api_events

import com.astrainteractive.empire_items.meg.BossBarController
import com.astrainteractive.empire_items.meg.api.EmpireModelEngineAPI
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_itemss.api.play
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent

class ModelEngineEvent {
    val onMobSpawn = DSLEvent.event(EntitySpawnEvent::class.java) { e ->
        EmpireModelEngineAPI.processSpawnedMob(e.location, e.entity)
    }

    val onEntityTarget = DSLEvent.event(EntityTargetEvent::class.java) { e ->
        EmpireModelEngineAPI.onEntityTarget(e)
    }
    val onDeath = DSLEvent.event(EntityDeathEvent::class.java) { e ->
        val entity = EmpireModelEngineAPI.getEmpireEntity(e.entity) ?: return@event
        entity.ymlMob.events["onDeath"]?.playSound?.play(e.entity.location)
        BossBarController.onEntityDead(e.entity)
    }

    val entityAttackEvent = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        EmpireModelEngineAPI.performAttack(e)
    }
    val onEmpireEntityDamaged = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        val entity = EmpireModelEngineAPI.getEmpireEntity(e.entity) ?: return@event
        entity.ymlMob.events["onDamaged"]?.playSound?.play(e.entity.location)
        BossBarController.onEntityDamaged(e.entity)
    }

}