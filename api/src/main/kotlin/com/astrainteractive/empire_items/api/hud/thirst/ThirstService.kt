package com.astrainteractive.empire_items.api.hud.thirst

import com.astrainteractive.astralibs.utils.uuid
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

object ThirstService {
    /**
     * PlayerName to ThirstLevel
     */
    private val thirstsMap = mutableMapOf<String, Int>()
    fun removePlayer(player: Player) {
        val value = thirstsMap[player.uuid] ?: return
        ThirstModule.file.getConfig().set("stats." + player.uuid + ".thirst", value)
        ThirstModule.file.saveConfig()
        thirstsMap.remove(player.uuid)
    }

    fun addPlayer(player: Player) {
        val value = ThirstModule.file.getConfig().getInt("stats." + player.uuid + ".thirst", 21)
        thirstsMap[player.uuid] = value
    }

    fun update(player: Player, value: Int) {
        val old = thirstsMap[player.uuid] ?: return
        var new = max(min(21, old + value), 0)
        thirstsMap[player.uuid] = new
//        HudView.send(player, FontApi.map["thirst_${new}"] ?: return, "thirst",208)
    }

    fun getValue(player: Player): Int? {
        return thirstsMap[player.uuid]
    }
}