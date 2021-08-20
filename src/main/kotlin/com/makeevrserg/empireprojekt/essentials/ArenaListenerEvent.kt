//package com.com.makeevrserg.empireprojekt.essentials
//
//import com.makeevrserg.empireprojekt.EmpirePlugin
//import org.bukkit.Location
//import org.bukkit.entity.Player
//import org.bukkit.event.EventHandler
//import org.bukkit.event.Listener
//import org.bukkit.event.entity.EntityDamageByEntityEvent
//import org.bukkit.event.entity.PlayerDeathEvent
//import java.awt.Point
//import java.lang.Double.max
//import java.lang.Double.min
//import kotlin.math.max
//import kotlin.math.min
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
//    private fun Player.inLocation(p1:Point,p2:Point):Boolean{
//        if (this.location.x>max(p1.x,p2.x) || this.location.x<min(p1.x,p2.x))
//            return false
//        if (this.location.z>max(p1.y,p2.y) || this.location.z<min(p1.y,p2.y))
//            return false
//        return true
//    }
//
//    @EventHandler
//    fun onPlayerLowHP(e: EntityDamageByEntityEvent) {
//        if (e.entity !is Player)
//            return
//        val player = e.entity as Player
//        if (!(e.damage > player.health || e.finalDamage > player.health))
//            return
//        if (!player.inLocation(Point(100,200),Point(300,400)))
//            return
//        e.isCancelled = true
//
//    }
//
//
//    init {
//        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
//    }
//
//    fun onDisable() {
//        PlayerDeathEvent.getHandlerList().unregister(this)
//        EntityDamageByEntityEvent.getHandlerList().unregister(this)
//    }
//}