package com.makeevrserg.empireprojekt.events.empireevents

import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.persistence.PersistentDataType

class Vampirism : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this,plugin)
    }
    fun onDisable(){
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }
    @EventHandler
    private fun onEntityDamate(e: EntityDamageByEntityEvent) {
        if (e.damager !is Player) return
        val p = e.damager as Player
        val itemStack = p.inventory.itemInMainHand
        val itemMeta = itemStack.itemMeta ?: return
        val vampSize = itemMeta.persistentDataContainer
            .get(plugin.empireConstants.VAMPIRISM_ENCHANT, PersistentDataType.INTEGER)
            ?: return
        val damage = e.finalDamage
        val playerHealth = p.health
        val playerMaxHealth = p.maxHealth
        val toAddHealth: Double = damage * plugin.config.vampirismMultiplier + vampSize
        p.health = Math.min(toAddHealth + playerHealth, playerMaxHealth)
    }


}
