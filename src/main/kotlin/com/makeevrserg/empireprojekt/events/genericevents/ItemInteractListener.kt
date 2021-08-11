package com.makeevrserg.empireprojekt.events.genericevents

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import empirelibs.EmpireUtils
import empirelibs.getEmpireID
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerMoveEvent

class ItemInteractListener : Listener {

    init {
        instance.server.pluginManager.registerEvents(this, instance)
    }


    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        initEventByHandler(event.player, event.action.name)
    }

    @EventHandler
    fun onDrink(event: PlayerItemConsumeEvent) {
        initEventByHandler(event.player, event.eventName)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity !is Player)
            return
        initEventByHandler(event.entity as Player, event.eventName)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable {
            initEventByHandler(event.player, event.eventName)
        })
    }

    private fun initEventByHandler(p: Player, eventName: String) {
        if (eventName.equals("PlayerMoveEvent", ignoreCase = true))
            for (item in p.inventory.armorContents)
                GenericEventManager.handleEvent(item.getEmpireID(), p, eventName)

        GenericEventManager.handleEvent(p.inventory.itemInMainHand.getEmpireID(), p, eventName)
        GenericEventManager.handleEvent(p.inventory.itemInOffHand.getEmpireID(), p, eventName)

    }


    fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
        PlayerItemConsumeEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
    }


}