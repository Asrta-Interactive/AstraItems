package com.astrainteractive.empire_items.modules.hud.thirst

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.empire_items.empire_items.util.AsyncHelper
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.math.abs

class Events : EventListener {
    operator fun Boolean.times(value: Float): Float {
        return if (this) value else 1f
    }

    operator fun Boolean.div(value: Float): Float {
        return if (this) 1 / value else 1f
    }

    private val amountMap = mutableMapOf<String, Float>()

    private fun valueByFactors(value: Double, player: Player): Float = valueByFactors(value.toFloat(), player)
    private fun valueByFactors(value: Float, player: Player): Float {
        val rain = player.location.world.isClearWeather / 2f
        val sprint = player.isSprinting * 2f
        val shift = player.isSneaking / 4f
        return value * rain * sprint * shift

    }

    @EventHandler
    fun onDrink(e: PlayerItemConsumeEvent) {
        when (e.item.type) {
            Material.POTION, Material.LINGERING_POTION, Material.SPLASH_POTION -> ThirstService.update(e.player, 5)
        }
    }


    @EventHandler
    fun PlayerMoveEvent(e: PlayerMoveEvent) {
        AsyncHelper.runBackground {
            val distance = valueByFactors(abs(e.to.distance(e.from)), e.player)
            var amount = amountMap[e.player.name] ?: 0f
            amount += distance.toFloat()
            if (amount > 10) {
                amount = 0f
                ThirstService.update(e.player, -1)
            }
            amountMap[e.player.name] = amount
        }
    }

    @EventHandler
    fun playerJoin(e: PlayerJoinEvent) {
        ThirstService.addPlayer(e.player)
        amountMap.remove(e.player.name)
    }

    @EventHandler
    fun playerJoin(e: PlayerQuitEvent) {
        ThirstService.removePlayer(e.player)
        amountMap[e.player.name] = 0f

    }

    override fun onDisable() {

    }
}