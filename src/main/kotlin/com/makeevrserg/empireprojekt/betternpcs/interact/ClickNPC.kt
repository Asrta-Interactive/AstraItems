package com.makeevrserg.empireprojekt.betternpcs.interact


import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.betternpcs.BetterNPCManager
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.GenericEventManager
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

class ClickNPC : IEmpireListener {

    @EventHandler
    fun onClickNPC(e: RightClickNPC) {
        val player = e.player

        if (e.npc.npc.phrases?.isNotEmpty() == true) {
            val phrases = e.npc.npc.phrases
            val phrase = phrases[Random.nextInt(phrases.size)]
            e.player.sendMessage(phrase)
        }
        for (command in e.npc.npc.commands?: listOf()){
            if (command.asConsole)
                Bukkit.dispatchCommand(Bukkit.getServer().consoleSender,command.command)
            else
                player.performCommand(command.command)
        }
    }


    var eventTimer = System.currentTimeMillis()
    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        eventTimer = System.currentTimeMillis()
        BetterNPCManager.playerMoveEvent(player)
    }

    @EventHandler
    fun playerJoinEvent(e:PlayerJoinEvent){
        BetterNPCManager.playerJoinEvent(e.player)

    }
    @EventHandler
    fun playerQuitEvent(e:PlayerQuitEvent){
        BetterNPCManager.playerQuitEvent(e.player)
    }
    @EventHandler
    fun playerDeathEvent(e:PlayerDeathEvent){
        BetterNPCManager.playerQuitEvent(e.entity)
    }
    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    public override fun onDisable() {
        RightClickNPC.HANDLERS.unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
    }
}