package com.astrainteractive.empire_items.api.mobs.data

import com.astrainteractive.astralibs.getHEXString
import org.bukkit.configuration.ConfigurationSection

data class MobBossBar(
    val name:String,
    val color:String,
    val barStyle:String,
    val flags:List<String>
){
    companion object{
        fun getBar(s:ConfigurationSection?): MobBossBar? {
            s?:return null
            return MobBossBar(
                name = s.getHEXString("name",""),
                color = s.getString("color")?:"RED",
                barStyle = s.getString("barStyle")?:"SOLID",
                flags = s.getStringList("flags")

            )
        }
    }
}