package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.hasPersistentData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.inventory.ItemStack

class VoidTotemEvent {

    fun ItemStack.isDeathTotem() =
        itemMeta.hasPersistentData(BukkitConstants.VOID_TOTEM) == true

    val entityResurrectEvent = DSLEvent.event(EntityDamageEvent::class.java)  { e ->
        if (e.entity !is Player)
            return@event
        if (e.cause != EntityDamageEvent.DamageCause.VOID)
            return@event
        val p = e.entity as Player
        if (e.damage<p.health)
            return@event
        if (p.location.world?.name?.endsWith("_end")!=true)
            return@event
        if (!p.inventory.itemInMainHand.isDeathTotem() && !p.inventory.itemInOffHand.isDeathTotem())
            return@event
        val location = p.location.clone()
        if (location.y>0)
            return@event
        location.y = 256.0
        p.teleport(location)
        p.damage(999999.0)
        e.isCancelled = true


    }

}