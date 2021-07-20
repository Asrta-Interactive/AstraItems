//package com.makeevrserg.empireprojekt.essentials
//
//import com.makeevrserg.empireprojekt.EmpirePlugin
//import org.bukkit.Location
//import org.bukkit.entity.Player
//import org.bukkit.event.EventHandler
//import org.bukkit.event.Listener
//import org.bukkit.event.entity.EntityDamageByEntityEvent
//import org.bukkit.event.entity.PlayerDeathEvent
//
//class ArenaListener : Listener {
//
//
//    @EventHandler
//    fun onPlayerDeath(e: PlayerDeathEvent) {
//        val player = e.entity
//
//    }
//
//    private fun Player.inLocation(P1: Location,endLoc:Location){
//        val playerLocation = this.location
//        val P2 = Location(P1.world,endLoc.x,P1.y,P1.z)
//
//
//    }
//
//    @EventHandler
//    fun onPlayerLowHP(e: EntityDamageByEntityEvent) {
//        if (e.entity !is Player)
//            return
//        val player = e.entity as Player
//
//        if (!(e.damage > player.health || e.finalDamage > player.health))
//            return
//        e.isCancelled = true
//        player.teleport(Location(player.location.world,))
//    }
//
//
//    init {
//        EmpirePlugin.plugin.server.pluginManager.registerEvents(this, EmpirePlugin.plugin)
//    }
//
//    fun onDisable() {
//        PlayerDeathEvent.getHandlerList().unregister(this)
//        EntityDamageByEntityEvent.getHandlerList().unregister(this)
//    }
//}