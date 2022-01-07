package com.astrainteractive.empire_items.empire_items.api.mobs.data

import org.bukkit.configuration.ConfigurationSection

data class SpawnInfo(val conditions: List<SpawnCondition>) {
    companion object {
        fun fromSection(s: ConfigurationSection?): SpawnInfo {
            val list = s?.getKeys(false)?.mapNotNull { key ->
                SpawnCondition.get(s.getConfigurationSection(key))
            }?: listOf()
            return SpawnInfo(list)
        }
    }
}