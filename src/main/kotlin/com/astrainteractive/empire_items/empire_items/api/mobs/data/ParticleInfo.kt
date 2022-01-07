package com.astrainteractive.empire_items.empire_items.api.mobs.data

import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

data class ParticleInfo(
    val cooldown: Int,
    val name: String,
    private val _color: String?,
    val amount: Int,
    val extra: Double
) {
    val color: Color?
        get() = if (_color != null) Color.fromRGB(Integer.decode(_color?.replace("#", "0x"))) else null

    companion object {
        fun fromSection(s: ConfigurationSection?): ParticleInfo? {
            s ?: return null
            return ParticleInfo(
                cooldown = s.getInt("cooldown", 1000),
                name = s.getString("name", Particle.REDSTONE.name)!!,
                _color = s.getString("color"),
                amount = s.getInt("amount", 10),
                extra = s.getDouble("extra", 0.005)
            )
        }
    }
}
