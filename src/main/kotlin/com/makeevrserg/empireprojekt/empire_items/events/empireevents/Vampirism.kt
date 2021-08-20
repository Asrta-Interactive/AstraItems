package com.makeevrserg.empireprojekt.empire_items.events.empireevents

import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.persistence.PersistentDataType

class Vampirism : IEmpireListener {



    override fun onDisable(){
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }
    @EventHandler
    private fun onEntityDamate(e: EntityDamageByEntityEvent) {
        if (e.damager !is Player) return
        val p = e.damager as Player

        val itemStack = p.inventory.itemInMainHand
        val itemMeta = itemStack.itemMeta ?: return
        val vampSize = itemMeta.persistentDataContainer
            .get(BetterConstants.VAMPIRISM_ENCHANT.value, PersistentDataType.DOUBLE)
            ?: return
        val damage = e.finalDamage
        val playerHealth = p.health
        val playerMaxHealth = p.maxHealth
        val toAddHealth: Double = damage * vampSize
        p.health = (toAddHealth + playerHealth).coerceAtMost(playerMaxHealth)
    }


}
