package com.astrainteractive.empire_items.api.mobs.data

import com.astrainteractive.empire_items.api.utils.getDoubleOrNull
import org.bukkit.configuration.ConfigurationSection

data class EmpireMobSound(
    val sound: String,
    val cooldown: Int?,
    val randomSound: Double?
) {
    companion object{
        fun get(s:ConfigurationSection?): EmpireMobSound? {
            s?:return null
            return EmpireMobSound(
                sound = s.getString("sound")?:return null,
                cooldown = s.getInt("cooldown"),
                randomSound = s.getDoubleOrNull("randomSound"),
            )
        }
    }
}