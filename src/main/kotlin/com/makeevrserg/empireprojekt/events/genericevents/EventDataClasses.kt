package com.makeevrserg.empireprojekt.events.genericevents

import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


//yml_items.item.interact

data class EmpireEvent(
    val eventName: List<String> = mutableListOf(),
    val potionEffectsAdd: MutableList<PotionEffect> = mutableListOf(),
    val potionEffectRemove: MutableList<PotionEffectType> = mutableListOf(),
    val soundsPlay: MutableList<EmpireSoundEvent> = mutableListOf(),
    val particlesPlay: MutableList<EmpireParticleEvent> = mutableListOf(),
    val commandsPlay: MutableList<EmpireCommandEvent> = mutableListOf(),
    val cooldown: Int = 0
) {


    private constructor(builder: Builder) : this(
        builder.eventName,
        builder.potionEffectsAdd,
        builder.potionEffectRemove,
        builder.soundsPlay,
        builder.particlesPlay,
        builder.commandsPlay,
        builder.cooldown
    )


    class Builder {

        var eventName: List<String> = mutableListOf()
            private set
        var potionEffectsAdd: MutableList<PotionEffect> = mutableListOf()
            private set
        var potionEffectRemove: MutableList<PotionEffectType> = mutableListOf()
            private set
        var soundsPlay: MutableList<EmpireSoundEvent> = mutableListOf()
            private set
        var particlesPlay: MutableList<EmpireParticleEvent> = mutableListOf()
            private set
        var commandsPlay: MutableList<EmpireCommandEvent> = mutableListOf()
            private set
        var cooldown: Int = 0
            private set

        fun eventName(eventName: List<String>) =
            apply { this.eventName = eventName }

        fun potionEffectsAdd(potionEffectsAdd: MutableList<PotionEffect>) =
            apply { this.potionEffectsAdd = potionEffectsAdd }

        fun potionEffectRemove(potionEffectRemove: MutableList<PotionEffectType>) =
            apply { this.potionEffectRemove = potionEffectRemove }

        fun soundsPlay(soundsPlay: MutableList<EmpireSoundEvent>) =
            apply { this.soundsPlay = soundsPlay }

        fun particlesPlay(particlesPlay: MutableList<EmpireParticleEvent>) =
            apply { this.particlesPlay = particlesPlay }

        fun commandsPlay(commandsPlay: MutableList<EmpireCommandEvent>) =
            apply { this.commandsPlay = commandsPlay }

        fun cooldown(cooldown: Int) =
            apply { this.cooldown = cooldown }

        fun build() = EmpireEvent(this)
    }
}

data class EmpireSoundEvent(
    val name: String,
    val volume: Double,
    val pitch: Double
)

data class EmpireParticleEvent(
    val name: String,
    val count: Int,
    val time: Double
)

data class EmpireCommandEvent(
    val command: String,
    val asConsole: Boolean
)
