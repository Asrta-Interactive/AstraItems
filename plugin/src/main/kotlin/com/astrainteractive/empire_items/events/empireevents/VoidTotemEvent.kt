package com.astrainteractive.empire_items.events.empireevents

import com.astrainteractive.empire_itemss.api.utils.BukkitConstants
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.utils.persistence.Persistence.hasPersistentData

class VoidTotemEvent {

    fun ItemStack.isDeathTotem() =
        itemMeta.hasPersistentData(BukkitConstants.VOID_TOTEM) == true

    val entityResurrectEvent = DSLEvent.event<EntityDamageEvent>  { e ->
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