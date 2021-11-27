package com.astrainteractive.empireprojekt.empire_items.events.empireevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.persistence.PersistentDataType

class Vampirism : IAstraListener {



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
            .get(BukkitConstants.VAMPIRISM_ENCHANT.value, BukkitConstants.VAMPIRISM_ENCHANT.dataType)
            ?: return
        val damage = e.finalDamage
        val playerHealth = p.health
        val playerMaxHealth = p.maxHealth
        val toAddHealth: Double = damage * vampSize
        p.health = (toAddHealth + playerHealth).coerceAtMost(playerMaxHealth)
    }


}
