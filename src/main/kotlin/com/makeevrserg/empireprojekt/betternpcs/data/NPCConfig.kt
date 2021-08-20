package com.makeevrserg.empireprojekt.betternpcs.data

import com.google.gson.reflect.TypeToken
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.betternpcs.BetterNPCManager
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser
import makeevrserg.empireprojekt.random_items.data.RandomItem

data class NPCConfig(
    val radiusTrack: Int = 20,
    val radiusHide: Int = 50,
    val npcRemoveListTime: Long = 50,
    val spawnNPCPacketTime: Long = 100,
    val npcTrackTime: Int = 300
) {
    companion object {

        fun new() = EmpireYamlParser.fromYAML<NPCConfig>(
            BetterNPCManager.fileManager.getConfig().getConfigurationSection("config"),
            NPCConfig::class.java
        )

    }
}