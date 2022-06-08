package com.astrainteractive.empire_items.api.items.data.interact

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.valueOfOrNull
import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

data class PlayParticle(
    val name: String,
    val count: Int,
    val time: Double,
    val length:Int,
) {
    fun play(location: Location) {
        val particle = valueOfOrNull<Particle>(name) ?: kotlin.run {
            Logger.warn("No effect named ${name}")
            return
        }
        ParticleBuilder(particle).count(count).extra(time).location(location).spawn()
    }

    companion object {
        fun getMultiPlayParticle(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getSinglePlayParticle(s.getConfigurationSection(it))
            }

        fun getSinglePlayParticle(s: ConfigurationSection?): PlayParticle? {
            return PlayParticle(
                name = s?.getString("name") ?: return null,
                count = s.getInt("count"),
                time = s.getDouble("time"),
                length = s.getInt("length")

            )
        }
    }
}
