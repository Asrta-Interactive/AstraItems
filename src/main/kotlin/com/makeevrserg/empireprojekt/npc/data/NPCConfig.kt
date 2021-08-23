package com.makeevrserg.empireprojekt.npc.data

import com.makeevrserg.empireprojekt.npc.NPCManager
import com.makeevrserg.empireprojekt.empirelibs.EmpireYamlParser

data class NPCConfig(
    val radiusTrack: Int = 20,
    val radiusHide: Int = 50,
    val npcRemoveListTime: Long = 50,
    val spawnNPCPacketTime: Long = 100,
    val npcTrackTime: Int = 300
) {
    companion object {

        fun new() = EmpireYamlParser.fromYAML<NPCConfig>(
            NPCManager.fileManager.getConfig().getConfigurationSection("config"),
            NPCConfig::class.java
        )

    }
}