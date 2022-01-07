package com.astrainteractive.empire_items.empire_items.api.mobs.data

import com.astrainteractive.empire_items.empire_items.api.items.data.AstraItem.Companion.getIntOrNull
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import java.nio.file.Path

data class CustomSpawn(
    val time: Long,
    val spawnIfSpawned: Boolean,
    val location: Location,
    val range: Int
) {
    companion object {
        private fun ConfigurationSection.getEmpireLocation(path: String): Location? {
            val x = getDouble("$path.x")
            val y = getDouble("$path.y")
            val z = getDouble("$path.z")
            val worldName = getString("$path.world") ?: return null
            val world = Bukkit.getWorld(worldName)
            return Location(world, x, y, z)


        }
        fun fromSection(s: ConfigurationSection?): CustomSpawn? {
            s ?: return null
            return CustomSpawn(
                time = s.getInt("time", 30 * 60 * 1000)?.toLong(),
                spawnIfSpawned = s.getBoolean("spawnIfSpawned", true),
                location = s.getEmpireLocation("location") ?: return null,
                range = s.getInt("range", 0)
            )
        }
    }
}
