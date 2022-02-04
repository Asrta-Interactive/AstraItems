package com.astrainteractive.empire_items.empire_items.events.blocks

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.awt.Point
import kotlin.random.Random

object GenerationUtils {
    /**
     * Содержит ли чанк игрока, или есть ли вблизи viewDistance игроки
     */
    fun Chunk.containsPlayers(): Boolean {
        val list = Bukkit.getOnlinePlayers().filter { this.distanceToPlayer(it) < Bukkit.getServer().viewDistance }
        return list.isNotEmpty()
    }
    /**
     * Получаем рандомное направление чтобы получить связный блок
     */
    fun getRandomBlockFace(): BlockFace {
        val faces = BlockFace.values()
        return faces[Random.nextInt(faces.size)]
    }

    /**
     * Дистанция от чанка до игрока в 2D координатах
     */
    fun Chunk.distanceToPlayer(p: Player): Double {
        val p1 = Point(x, z)
        val p2 = Point(p.location.chunk.x, p.location.chunk.z)
        return p1.distance(p2)
    }
}