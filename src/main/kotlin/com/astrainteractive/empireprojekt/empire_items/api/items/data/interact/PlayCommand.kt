package com.astrainteractive.empireprojekt.empire_items.api.items.data.interact

import org.bukkit.configuration.ConfigurationSection

data class PlayCommand(
    val command:String,
    val asConsole:Boolean
){
    companion object{
        fun getMultiPlayCommand(s:ConfigurationSection?) =
            s?.getKeys(false)?.mapNotNull {
                getSinglePlayCommand(s.getConfigurationSection(it))
            }
        private fun getSinglePlayCommand(s:ConfigurationSection?): PlayCommand? {
            return PlayCommand(s?.getString("command")?:return null,s.getBoolean("asConsole",false))
        }

    }
}
