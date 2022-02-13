package com.astrainteractive.empire_items.empire_items.api.mobs.data

import org.bukkit.configuration.ConfigurationSection

data class MobPotionEffect(
    val effect: String,
    val level: Int,
    val duration: Int
) {
    companion object {
        fun getAll(s: ConfigurationSection?): List<MobPotionEffect> {
            return s?.getKeys(false)?.mapNotNull { effect ->
                val effectSection = s.getConfigurationSection(effect) ?: return@mapNotNull null
                return@mapNotNull MobPotionEffect(
                    effect = effect,
                    level = effectSection.getInt("level", 1),
                    duration = effectSection.getInt("duration", Int.MAX_VALUE)
                )
            } ?: listOf()
        }
    }
}