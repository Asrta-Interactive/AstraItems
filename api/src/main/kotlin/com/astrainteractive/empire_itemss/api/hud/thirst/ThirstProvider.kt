package com.astrainteractive.empire_itemss.api.hud.thirst

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.hud.IHudValueProvider
import com.astrainteractive.empire_itemss.api.hud.PlayerHud
import org.bukkit.entity.Player
import java.util.*

class ThirstProvider(val percentProvider: (UUID) -> Float) : IHudValueProvider {
    override val id: String
        get() = "thirst"
    override val position: Int
        get() = 128 + 64 + 16

    override fun provideHud(player: Player): PlayerHud {
        val minValue = 0
        val maxValue = 21
        val percent = percentProvider(player.uniqueId)
        val value = (maxValue * percent).toInt().coerceIn(minValue, maxValue)
        val id = "thirst_$value"
        val font = EmpireItemsAPI.fontByID[id]!!
        return PlayerHud(id, position, font)
    }
}

class TestProvider(val key: String, override val position: Int = 0) : IHudValueProvider {
    override val id: String = UUID.randomUUID().toString()
    override fun provideHud(player: Player): PlayerHud {
        val font = EmpireItemsAPI.fontByID[key]!!
        return PlayerHud(id, position, font)
    }
}