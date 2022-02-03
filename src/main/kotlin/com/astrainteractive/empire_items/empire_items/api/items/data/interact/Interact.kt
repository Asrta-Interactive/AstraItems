package com.astrainteractive.empire_items.empire_items.api.items.data.interact

import com.astrainteractive.empire_items.empire_items.api.items.data.EmpireItem.Companion.getIntOrNull
import org.bukkit.configuration.ConfigurationSection

data class Interact(
    val eventList:List<String>?,
    val cooldown:Int?,
    val playParticle:List<PlayParticle>?,
    val playSound:List<PlaySound>?,
    val playPotionEffect:List<PlayPotionEffect>?,
    val potionEffectsRemove:List<String>,
    val playCommand:List<PlayCommand>?,
){
    companion object{
        fun getMultiInteract(s:ConfigurationSection?)=
            s?.getKeys(false)?.mapNotNull {
                getSingleInteract(s.getConfigurationSection(it))
            }

        private fun getSingleInteract(s:ConfigurationSection?): Interact? {
            val eventList = s?.getStringList("eventList")?:return null
            val cooldown = s.getIntOrNull("cooldown")
            val playParticle = PlayParticle.getMultiPlayParticle(s.getConfigurationSection("playParticle"))
            val playSound = PlaySound.getMultiPlaySound(s.getConfigurationSection("playSound"))
            val playPotionEffect =
                PlayPotionEffect.getMultiPlayPotionEffect(s.getConfigurationSection("playPotionEffect"))
            val potionEffectsRemove = s.getStringList("potionEffectsRemove")
            val playCommand = PlayCommand.getMultiPlayCommand(s.getConfigurationSection("playCommand"))
            return Interact(eventList = eventList,
                cooldown = cooldown,
                playParticle =  playParticle,
                playSound = playSound,
                playPotionEffect = playPotionEffect,
                potionEffectsRemove = potionEffectsRemove,
                playCommand = playCommand
            )

        }
    }
}
