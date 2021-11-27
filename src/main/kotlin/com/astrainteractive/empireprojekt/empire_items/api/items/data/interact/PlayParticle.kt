package com.astrainteractive.empireprojekt.empire_items.api.items.data.interact

import org.bukkit.configuration.ConfigurationSection

data class PlayParticle(
    val name: String,
    val count: Int,
    val time: Double
) {
    companion object {
        fun getMultiPlayParticle(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getSinglePlayParticle(s.getConfigurationSection(it))
            }

        private fun getSinglePlayParticle(s: ConfigurationSection?): PlayParticle? {
            return PlayParticle(s?.getString("name") ?: return null, s.getInt("count"), s.getDouble("time"))
        }
    }
}
