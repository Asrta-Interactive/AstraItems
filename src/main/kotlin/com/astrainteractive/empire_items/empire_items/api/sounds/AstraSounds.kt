package com.astrainteractive.empire_items.empire_items.api.sounds

import com.astrainteractive.empire_items.empire_items.api.utils.getCustomItemsFiles
import org.bukkit.configuration.ConfigurationSection

data class AstraSounds(
    @Transient
    val id:String,
    @Transient
    val namespace: String,
    val sounds:List<String>
){
    companion object{
        fun getSounds() = getCustomItemsFiles()?.mapNotNull {
            val fileConfig = it.getConfig()
            val section = fileConfig.getConfigurationSection("sounds")
            section?.getKeys(false)?.mapNotNull {
                getSound(section.getConfigurationSection(it),fileConfig.getString("namespace","empire_items")!!)
            }
        }?.flatten()?: listOf()
        fun getSound(s:ConfigurationSection?,namespace:String): AstraSounds? {
            s?:return null
            val id = s.name
            val sounds = s.getStringList("sounds")
            if (sounds.isEmpty())
                return null
            return AstraSounds(id = id,sounds = sounds,namespace = namespace)

        }

    }
}