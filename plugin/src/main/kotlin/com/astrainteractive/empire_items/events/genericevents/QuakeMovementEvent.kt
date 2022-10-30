package com.astrainteractive.empire_items.events.genericevents

import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.utils.uuid
import com.astrainteractive.empire_items.util.Timer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import kotlin.math.*

class QuakeMovementEvent : EventListener {
    val MIN = 50
    val MAX = 250
    val times = mutableMapOf<String, Timer>()

    private fun sendActionBar(player: Player, playerSpeed: Int) {
        val text3 = "☄${playerSpeed} "
        player.sendActionBar(text3)
    }

    private val playerSpeeds = mutableMapOf<String, Int>()

    private fun getPlayerSpeed(player: Player): Int {
        return playerSpeeds[player.uuid] ?: MIN
    }

    private fun setPlayerSpeed(player: Player, speed: Int) {
        playerSpeeds[player.uuid] = speed
    }

    private fun Player.quakeSpeed(_speed: Int) {
        if (!this.isSprinting)
            return
        if (this.location.block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).type == Material.AIR)
            return
        this.velocity = this.velocity.add(this.location.direction.multiply(_speed.toSpeed()))
    }

    private fun Int.plus(other: Int, max: Int): Int =
        min(this + other, max)


    private fun Int.sub(other: Int, min: Int): Int =
        max(this - other, min)

    private fun Player.mIsJumping(): Boolean = location.block.getRelative(BlockFace.DOWN).type != Material.AIR

    private fun Double.round(dec: Int): Double {
        val round = 10.0.pow(dec.toDouble())
        return (this * round).roundToInt() / round
    }

    fun Int.toSpeed(): Float = this / 1000f
    fun Float.round(dec: Int): Float = this.toDouble().round(dec).toFloat()

    private fun createVector(start: Location, end: Location): Vector {
        return Vector(end.x - start.x, end.y - start.y, end.z - start.z)
    }

    private fun vectorModule(vector: Vector) = sqrt(vector.x.pow(2) + vector.y.pow(2) + vector.z.pow(2))
    private fun multiplyVector(v1: Vector, v2: Vector) = v1.x * v2.x + v1.y + v2.y + v1.z * v2.z


    val timer = kotlin.concurrent.timer("QuakeMove", true, 0L, 10L) {
        Bukkit.getOnlinePlayers().forEach { player ->
            var playerSpeed = getPlayerSpeed(player)
            if (!player.isSprinting) {
                playerSpeed -= 10
                playerSpeed = max(MIN, min(playerSpeed, MAX))
                setPlayerSpeed(player, playerSpeed)
                sendActionBar(player, playerSpeed)
                return@forEach
            }
        }
    }

    val speed = kotlin.concurrent.timer("speed", true, 0L, 100L) {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (!player.isSprinting) return@forEach
            player.quakeSpeed(getPlayerSpeed(player))
        }
    }

    private fun modifyPlayerSpeed(player: Player, wishDir: Vector = player.location.direction) {
        var playerSpeed = getPlayerSpeed(player)
        val velocity = player.velocity
        val currentSpeed = abs(multiplyVector(wishDir, velocity) / vectorModule(wishDir)) * 10
        playerSpeed += currentSpeed.toInt() / 5
        playerSpeed = max(MIN, min(playerSpeed, MAX))
        setPlayerSpeed(player, playerSpeed)
        sendActionBar(player, playerSpeed)
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (!e.player.isSprinting)return
        val wishDir = createVector(e.from, e.to)
        modifyPlayerSpeed(e.player, wishDir)
    }

    override fun onDisable() {
        PlayerMoveEvent.getHandlerList().unregister(this)
        timer.cancel()
        speed.cancel()
    }
}