package com.astrainteractive.empire_items.empire_items.api.items.data.decoration

import com.google.gson.annotations.SerializedName
import org.bukkit.configuration.ConfigurationSection

data class Decoration(
    val breakSound: String?,
    val placeSound: String?,
    val height: Int,
    val width: Int,
    val length:Int
) {
    companion object {
        fun getDecoration(s: ConfigurationSection?): Decoration? {
            s ?: return null
            return Decoration(
                s.getString("breakSound"),
                s.getString("placeSound"),
                s.getInt("hitbox.height", 1),
                s.getInt("hitbox.width", 1),
                s.getInt("hitbox.length", 1),
            )
        }
    }
}