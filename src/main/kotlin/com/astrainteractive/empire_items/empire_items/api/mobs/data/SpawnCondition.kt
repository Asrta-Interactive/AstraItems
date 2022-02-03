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
                replace = s.getConfigurationSection("replace").getMap<String,Int>()?: mapOf(),
                customSpawn = CustomSpawn.fromSection(s.getConfigurationSection("custom"))
            )
        }
    }
}
inline fun <reified K,T> ConfigurationSection?.getMap(path:String): Map<K, T>? {
    return this?.getConfigurationSection(path).getMap()
}
inline fun <reified K,T> ConfigurationSection?.getMap(): Map<K, T>? {
    return  this?.getKeys(false)?.associate { key ->
        var first = when(K::class){
            Int::class->key.toIntOrNull()
            Double::class->key.toDoubleOrNull()
            Char::class->key.firstOrNull()
            else -> key
        }
        Pair(first as K, get(key) as T)
    }?.filter { it.key != null }
}