package com.makeevrserg.empireprojekt.NPCS.interact

import com.makeevrserg.empireprojekt.NPCS.NPCManager
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import me.clip.placeholderapi.PlaceholderAPI
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

        for (command in e.npc.commands) {
            if (command.as_console)
                if (instance.server.pluginManager.getPlugin("placeholderapi") != null)
                    instance.server.dispatchCommand(
                        instance.server.consoleSender,
                        PlaceholderAPI.setPlaceholders(player, command.command)
                    )
                else
                    instance.server.dispatchCommand(instance.server.consoleSender, command.command)
            else
                if (instance.server.pluginManager.getPlugin("placeholderapi") != null)
                    player.performCommand(PlaceholderAPI.setPlaceholders(player, command.command))
                else
                    player.performCommand(command.command)
        }
    }


    var eventTimer = System.currentTimeMillis()
    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
//        if (System.currentTimeMillis()-eventTimer<NPCManager.config.npcTrackTime)
//            return
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