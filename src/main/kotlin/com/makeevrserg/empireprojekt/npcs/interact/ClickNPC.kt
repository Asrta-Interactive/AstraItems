package com.makeevrserg.empireprojekt.npcs.interact

import com.makeevrserg.empireprojekt.npcs.NPCManager
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.GenericEventManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.random.Random

class ClickNPC : Listener {

    @EventHandler
    fun onClickNPC(e: RightClickNPC) {
        val player = e.player

        if (e.npc.phrases.isNotEmpty()) {
            val phrases = e.npc.phrases
            val phrase = phrases[Random.nextInt(phrases.size)]
            e.player.sendMessage(phrase)
        }
        GenericEventManager.manageCommand(player,e.npc.commands)
    }


    var eventTimer = System.currentTimeMillis()
    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        eventTimer = System.currentTimeMillis()
        NPCManager.playerMoveEvent(player)
    }

    @EventHandler
    fun playerJoinEvent(e:PlayerJoinEvent){
        NPCManager.playerJoinEvent(e.player)

    }
    @EventHandler
    fun playerQuitEvent(e:PlayerQuitEvent){
        NPCManager.playerQuitEvent(e.player)
    }
    @EventHandler
    fun playerDeathEvent(e:PlayerDeathEvent){
        NPCManager.playerQuitEvent(e.entity)
    }
    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    public fun onDisable() {
        RightClickNPC.HANDLERS.unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
    }
}