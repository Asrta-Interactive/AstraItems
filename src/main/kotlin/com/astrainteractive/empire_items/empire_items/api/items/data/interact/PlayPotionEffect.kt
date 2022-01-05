package com.astrainteractive.empire_items.empire_items.api.items.data.interact

import org.bukkit.configuration.ConfigurationSection

data class PlayPotionEffect(
    val effect:String,
    val amplifier:Int,
    val duration:Int
){
    companion object{
        fun getMultiPlayPotionEffect(s:ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getSinglePlayPotionEffect(s.getConfigurationSection(it))
            }
        private fun getSinglePlayPotionEffect(s:ConfigurationSection?): PlayPotionEffect? {
            return PlayPotionEffect(s?.getString("name") ?: return null, s.getInt("amplifier"), s.getInt("duration"))
        }
    }
}
