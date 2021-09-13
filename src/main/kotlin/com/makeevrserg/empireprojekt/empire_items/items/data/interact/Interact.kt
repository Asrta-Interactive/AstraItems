package com.makeevrserg.empireprojekt.items.data.interact

import com.google.gson.annotations.SerializedName
import com.makeevrserg.empireprojekt.empire_items.items.data.interact.Sound

data class Interact(
    @SerializedName("events_names")
    val eventList:List<String>?,
    @SerializedName("spawn_entity")
    val spawnEntity:Map<String,Int>?,
    @SerializedName("play_particle")
    val playParticle:List<ParticleEvent>?,
    @SerializedName("play_sound")
    val playSound:List<Sound>?,
    @SerializedName("potion_effect")
    val potionEffect:List<PotionEffectEvent>?,
    @SerializedName("potion_effect_remove")
    val potionEffectsRemove:List<String>,
    @SerializedName("play_command")
    val playCommand:List<CommandEvent>?,
    @SerializedName("cooldown")
    val cooldown:Int?
)
