package com.makeevrserg.empireprojekt.events.empireevents

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.util.BetterConstants
import empirelibs.IEmpireListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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
