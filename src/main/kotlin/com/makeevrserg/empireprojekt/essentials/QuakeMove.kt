//package com.com.makeevrserg.empireprojekt.essentials
//
//import com.makeevrserg.empireprojekt.EmpirePlugin
//import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
//import net.md_5.bungee.api.ChatMessageType
//import net.md_5.bungee.api.chat.TextComponent
//import org.bukkit.Material
//import org.bukkit.block.BlockFace
//import org.bukkit.entity.Player
//import org.bukkit.event.EventHandler
//import org.bukkit.event.Listener
//import org.bukkit.event.player.PlayerMoveEvent
//import java.awt.Color
//import kotlin.math.abs
//import kotlin.math.pow
//import kotlin.math.roundToInt
//
//
//class QuakeMove : Listener {
//    init {
//        plugin.server.pluginManager.registerEvents(this, EmpirePlugin.plugin)
//    }
//
//    private val playerSpeeds = mutableMapOf<Player, Double>()
//    fun Player.quakeSpeed(multiplier: Double) {
//        if (!this.isSprinting)
//            return
//        if (this.location.block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).type == Material.AIR)
//            return
//        val vel = this.velocity.add(this.location.direction.multiply(multiplier))
//        this.velocity = vel
//
//    }
//
//    fun Double.plus(other: Double, max: Double): Double {
//        if (this >= max)
//            return this
//        return this + other
//    }
//
//    fun Double.sub(other: Double, min: Double): Double {
//        if (this <= min)
//            return this
//        return this - other
//    }
//
//    fun Double.round(dec: Int): Double {
//        val round = 10.0.pow(dec.toDouble())
//        return (this * round).roundToInt() / round
//
//    }
//
//    fun Float.round(dec: Int): Float {
//        val round = 10.0.pow(dec.toDouble()).toFloat()
//        return (this * round).roundToInt() / round
//
//    }
//
//
//    fun Float.angleHEX(min: Double, max: Double): Color {
//
//        var p = this / max
//        if (min < 0) {
//            val mMin = min + abs(min)
//            val mMax = max + abs(max)
//            p = (this + abs(min)) / mMax
//        }
//
//
//        val colors= arrayOf(Color(224, 40, 40),
//            Color(224, 71, 40),
//            Color(224, 101, 40),
//            Color(224, 138, 40),
//            Color(224, 184, 40),
//            Color(135, 224, 40),
//            Color(83, 224, 40),
//            Color(40, 224, 71),
//            Color(40, 224, 132),
//            Color(40, 224, 190),
//            Color(40, 212, 224),
//            Color(40, 224, 190),
//            Color(40, 224, 132),
//            Color(40, 224, 71),
//            Color(83, 224, 40),
//            Color(135, 224, 40),
//            Color(224, 184, 40),
//            Color(224, 138, 40),
//            Color(224, 101, 40),
//            Color(224, 71, 40),
//            Color(224, 40, 40)
//        )
//        var index = (p*colors.size).toInt()
//
//        if (index>=colors.size)
//            index = colors.size-1
//        if (index<=0)
//            index = 0
//
//        val color = colors[index]
//        val hex = String.format("#%02x%02x%02x", color.red, color.green, color.blue)
//        return color
//
//
//    }
//
//    @EventHandler
//    fun onPlayerMoveEvent(e: PlayerMoveEvent) {
//        val player = e.player
//        e.to ?: return
//        val angle = player.location.direction.angle(e.to!!.direction)
//
//
//        if (!player.isSprinting)
//            playerSpeeds[player] = 0.1
//
//        if (abs(player.location.pitch) > 20)
//            playerSpeeds[player] = 0.1
//        player.spigot().sendMessage(
//            ChatMessageType.ACTION_BAR,
//            TextComponent("↔${angle.round(2)} ").apply { color = net.md_5.bungee.api.ChatColor.of(angle.angleHEX(-0.1,0.1)) },
//            TextComponent("↕${player.location.pitch.round(2)} ").apply { color = net.md_5.bungee.api.ChatColor.of(player.location.pitch.angleHEX(-25.0,25.0)) },
//                    TextComponent("☄${(playerSpeeds[player]!!.round(2)*1000).toFloat().toInt()} ").apply { color = net.md_5.bungee.api.ChatColor.of((playerSpeeds[player]!!.round(2)*1000).toFloat().angleHEX(100.0,400.0)) }
//        )
//        when {
//            angle < 0.2 -> {
//                playerSpeeds[player] = playerSpeeds[player]?.plus(0.0005, 0.25) ?: 0.01
//                player.quakeSpeed(playerSpeeds[player]!!)
//            }
//            angle < 1.0 -> {
//                playerSpeeds[player] = playerSpeeds[player]?.sub(0.02, 0.1) ?: 0.01
//                player.quakeSpeed(playerSpeeds[player]!!)
//            }
//            else -> {
//                playerSpeeds[player] = 0.01
//            }
//
//        }
//
//
//    }
//
//    fun onDisable() {
//        PlayerMoveEvent.getHandlerList().unregister(this)
//    }
//}