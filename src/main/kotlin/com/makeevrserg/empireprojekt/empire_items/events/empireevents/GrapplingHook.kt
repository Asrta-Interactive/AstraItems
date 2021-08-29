package com.makeevrserg.empireprojekt.empire_items.events.empireevents

import com.destroystokyo.paper.ParticleBuilder
import com.makeevrserg.empireprojekt.empire_items.util.BetterConstants
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.getEmpireID
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType

class GrapplingHook : IEmpireListener {



    val mapHooks = mutableMapOf<Player, Location>()






    @EventHandler
    fun playerHookShootEvent(e: PlayerInteractEvent) {
        val item = e.player.inventory.itemInMainHand
        item.getEmpireID()?:return
        if (item.itemMeta?.persistentDataContainer?.has(BetterConstants.GRAPPLING_HOOK.value, PersistentDataType.DOUBLE)!=true)
            return

        if (e.action==Action.LEFT_CLICK_AIR || e.action==Action.LEFT_CLICK_BLOCK){
            mapHooks.remove(e.player)
            return
        }

        val player = e.player
        if (mapHooks.containsKey(player)) {
            val location = mapHooks[player]!!.clone()
            mapHooks.remove(player)
            val v3 = location.subtract(player.location)

            val distance = location.distance(player.location)
            println(distance)
            if (distance > 400) {
                val l = player.location
                ParticleBuilder(Particle.GLOW)
                    .count(70)
                    .force(false)
                    .extra(0.06)
                    .data(null)
                    .location(l.world ?: return, l.x, l.y+1, l.z)
                    .spawn()

                return
            }
            val multiply = 0.4 - (distance/1000)
            println(multiply)
            player.velocity = v3.toVector().multiply(multiply)
            return
        }



        var l = player.location.clone().add(0.0, 1.5, 0.0)
        for (i in 0 until 300) {
            ParticleBuilder(Particle.REDSTONE)
                .count(20)
                .force(false)
                .extra(0.06)
                .data(null)
                .color(Color.BLACK)
                .location(l.world ?: return, l.x, l.y, l.z)
                .spawn()
            l =
                l.add(l.direction.x, l.direction.y - i / (200 * 0.9), l.direction.z)

            if (!l.block.isPassable) {
                mapHooks[player] = l.add(0.0, 1.0, 0.0)
                ParticleBuilder(Particle.SMOKE_LARGE)
                    .count(70)
                    .force(false)
                    .extra(0.06)
                    .data(null)
                    .location(l.world ?: return, l.x, l.y, l.z)
                    .spawn()
                return
            }
        }
    }

    @EventHandler
    fun playerLeaveEvent(e: PlayerQuitEvent) {
        mapHooks.remove(e.player)
    }

    override fun onDisable() {
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }
}