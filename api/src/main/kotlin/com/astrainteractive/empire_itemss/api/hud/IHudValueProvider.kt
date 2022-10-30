package com.astrainteractive.empire_itemss.api.hud

import org.bukkit.entity.Player

interface IHudValueProvider {
    val id: String
    val position: Int
    fun provideHud(player: Player): PlayerHud
}