package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.EventListener
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

class CatKillEvent: EventListener {


    @EventHandler
    private fun onCatHurtEvent(e:EntityDamageEvent){
        val entity = e.entity
        if (entity.type!=EntityType.CAT && entity.type!=EntityType.OCELOT || entity.type!=EntityType.PARROT)
            return
        e.damage = 0.0
        e.isCancelled = true
    }

    override fun onDisable() {
        EntityDamageEvent.getHandlerList().unregister(this)
    }
}