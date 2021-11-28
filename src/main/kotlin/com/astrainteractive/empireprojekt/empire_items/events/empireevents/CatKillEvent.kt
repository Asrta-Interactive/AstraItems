package com.astrainteractive.empireprojekt.empire_items.events.empireevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.toAstraItemOrItem
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empireprojekt.empire_items.api.utils.getPersistentData
import com.astrainteractive.empireprojekt.empire_items.api.utils.hasPersistentData
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Slime
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class CatKillEvent: IAstraListener {


    @EventHandler
    private fun onCatHurtEvent(e:EntityDamageEvent){
        val entity = e.entity
        if (entity.type!=EntityType.CAT && entity.type!=EntityType.OCELOT)
            return
        e.damage = 0.0
        e.isCancelled = true
    }

    override fun onDisable() {
        EntityDamageEvent.getHandlerList().unregister(this)
    }
}