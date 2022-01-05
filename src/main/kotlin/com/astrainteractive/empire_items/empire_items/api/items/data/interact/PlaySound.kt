package com.astrainteractive.empire_items.empire_items.api.items.data.interact

import com.astrainteractive.astralibs.getFloat
import org.bukkit.configuration.ConfigurationSection

data class PlaySound(
    val name:String,
    val volume:Float?,
    val pitch:Float?
){
    companion object{

        fun getMultiPlaySound(s: ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getSinglePlaySound(s.getConfigurationSection(it))
            }
        fun getSinglePlaySound(s:ConfigurationSection?): PlaySound? {
            return PlaySound(s?.getString("name")?:return null,s.getFloat("volume",0f),s.getFloat("pitch",0f))
        }
    }
}