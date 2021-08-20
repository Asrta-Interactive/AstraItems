package com.makeevrserg.empireprojekt.empire_items.events.mobs.data

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser

data class EmpireMob(
    @SerializedName("id")
    val id: String,
    @SerializedName("idle_animation")
    val idleAnimation: String,
    @SerializedName("walk_animation")
    val walkAnimation: String,
    @SerializedName("attack_animation")
    val attackAnimation: String?,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("sound_idle")
    val soundIdle: String?,
    @SerializedName("sound_die")
    val soundDie: String,
    @SerializedName("sound_hurt")
    val soundHurt: String,
    @SerializedName("is_silent")
    val isSilent: Boolean,
    @SerializedName("ai")
    val ai: String,
    @SerializedName("use_armor_stand")
    val useArmorStand: Boolean,
    @SerializedName("small_armor_stand")
    val smallArmorStand: Boolean,
    @SerializedName("attributes")
    val attributes: List<Attributes>,
    @SerializedName("replace_mob_spawn")
    val mobSpawnReplace: List<MobSpawnReplace>
) {

    public lateinit var mobByMap: Map<String, Double>
        private set
    init {
        initMobReplaceByName()
    }

    private fun initMobReplaceByName() {
        val map = mutableMapOf<String, Double>()
        for (mobRepl in mobSpawnReplace)
            map[mobRepl.type] = mobRepl.chance
        mobByMap = map
    }

    enum class STATE {
        WALK, ATTACK, IDLE
    }

    companion object {

        fun new(): List<EmpireMob> {
            return EmpireYamlParser.fromYAML<List<EmpireMob>>(
                EmpirePlugin.empireFiles.mobsFile.getConfig(),
                object : TypeToken<List<EmpireMob?>?>() {}.type,
                listOf("mobs")
            )!!
        }
    }
}