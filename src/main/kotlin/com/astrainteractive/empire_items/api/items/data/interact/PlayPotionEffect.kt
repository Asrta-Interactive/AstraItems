package com.astrainteractive.empire_items.api.items.data.interact

import com.astrainteractive.astralibs.Logger
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

data class PlayPotionEffect(
    val effect:String,
    val amplifier:Int,
    val duration:Int
){
    fun play(ent: LivingEntity?) {
        val effect = PotionEffectType.getByName(effect)?: kotlin.run {
            Logger.warn("No effect named $effect")
            return
        }
        ent?.addPotionEffect(PotionEffect(effect,duration,amplifier))
    }

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
