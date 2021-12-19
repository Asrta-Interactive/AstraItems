package com.astrainteractive.empireprojekt.empire_items.events.empireevents

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getAstraID
import com.astrainteractive.empireprojekt.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empireprojekt.empire_items.api.utils.hasPersistentData
import com.destroystokyo.paper.ParticleBuilder

import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class GrapplingHook : IAstraListener {




    private val mapHooks = mutableMapOf<String, Location>()






    @EventHandler
    fun playerHookShootEvent(e: PlayerInteractEvent) {
        val item = e.player.inventory.itemInMainHand
        item.getAstraID()?:return
        if (item.itemMeta.hasPersistentData(BukkitConstants.GRAPPLING_HOOK)!=true)
            return

        if (e.action==Action.LEFT_CLICK_AIR || e.action==Action.LEFT_CLICK_BLOCK){
            mapHooks.remove(e.player.name)
            return
        }
        if (e.player.gameMode!=GameMode.CREATIVE && (e.player as HumanEntity).hasCooldown(item.type))
            return

        val player = e.player
        if (mapHooks.containsKey(player.name)) {
            val location = mapHooks[player.name]!!.clone()
            mapHooks.remove(player.name)
            val v3 = location.clone().subtract(player.location)

            val distance = location.clone().distance(player.location)

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
            player.velocity = v3.toVector().multiply(multiply/2)
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING,25,1,false,false,false))
            (e.player as HumanEntity).setCooldown(item.type,50)
            return
        }



        var l = player.location.clone().add(0.0, 1.5, 0.0)
        for (i in 0 until 200) {
            ParticleBuilder(Particle.REDSTONE)
                .count(20)
                .force(true)
                .extra(0.06)
                .data(null)
                .color(Color.BLACK)
                .location(l.world ?: return, l.x, l.y, l.z)
                .spawn()
            l =
                l.add(l.direction.x, l.direction.y - i / (350 * 0.9), l.direction.z)

            if (!l.block.isPassable) {
                mapHooks[player.name] = l.add(0.0, 1.0, 0.0)
                ParticleBuilder(Particle.SMOKE_LARGE)
                    .count(70)
                    .force(true)
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
        mapHooks.remove(e.player.name)
    }

    override fun onDisable() {
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
    }
}