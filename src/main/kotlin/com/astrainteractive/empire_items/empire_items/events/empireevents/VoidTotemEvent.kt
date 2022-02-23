package com.astrainteractive.empire_items.empire_items.events.empireevents

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.hasPersistentData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.inventory.ItemStack

class VoidTotemEvent : EventListener {

    fun ItemStack.isDeathTotem() =
        itemMeta.hasPersistentData(BukkitConstants.VOID_TOTEM) == true

    @EventHandler
    private fun entityResurrectEvent(e: EntityDamageEvent) {
        if (e.entity !is Player)
            return
        if (e.cause != EntityDamageEvent.DamageCause.VOID)
            return
        val p = e.entity as Player
        if (e.damage<p.health)
            return
        if (p.location.world?.name?.endsWith("_end")!=true)
            return
        if (!p.inventory.itemInMainHand.isDeathTotem() && !p.inventory.itemInOffHand.isDeathTotem())
            return
        val location = p.location.clone()
        if (location.y>0)
            return
        location.y = 256.0
        p.teleport(location)
        p.damage(999999.0)
        e.isCancelled = true


    }

    override fun onDisable() {
        EntityResurrectEvent.getHandlerList().unregister(this)
    }
}