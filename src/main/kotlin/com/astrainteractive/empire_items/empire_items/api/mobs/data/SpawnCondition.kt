package com.astrainteractive.empire_items.empire_items.api.mobs.data

import com.astrainteractive.astralibs.valueOfOrNull
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.ConfigurationSection

data class SpawnCondition(
    val biomes: List<String>,
    val minY: Int,
    val maxY: Int,
    val replace: Map<String, Int>,
    val customSpawn: CustomSpawn?
) {
    companion object {
        fun get(s: ConfigurationSection?): SpawnCondition? {
            s ?: return null
            return SpawnCondition(
                biomes = s.getStringList("biomes"),
                minY = s.getInt("minY", 0),
                maxY = s.getInt("maxY", 0),
                replace = s.getConfigurationSection("replace").getMap<Int>(),
                customSpawn = CustomSpawn.fromSection(s.getConfigurationSection("custom"))
            )
        }
    }
}

fun <T> ConfigurationSection?.getMap(): Map<String, T> {
    return this?.getKeys(false)?.associate { key ->
        Pair(key, get(key) as T)
    }?.filter { it.key != null } as Map<String, T>
}