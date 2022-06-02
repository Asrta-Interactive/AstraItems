package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerQuitEvent

class CatKillEvent{


    val onCatHurtEvent = DSLEvent.event(EntityDamageEvent::class.java)  { e ->
        val entity = e.entity
        if (entity.type!=EntityType.CAT && entity.type!=EntityType.OCELOT || entity.type!=EntityType.PARROT)
            return@event
        e.damage = 0.0
        e.isCancelled = true
    }
}