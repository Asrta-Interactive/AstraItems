package com.makeevrserg.empireprojekt.betternpcs

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.betternpcs.data.EmpireNPC
import com.makeevrserg.empireprojekt.betternpcs.data.NPCConfig
import com.makeevrserg.empireprojekt.betternpcs.interact.EventManager
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.FileManager
import com.makeevrserg.empireprojekt.empirelibs.runTaskAsynchronously
import org.bukkit.entity.Player
import kotlin.math.abs

class BetterNPCManager {
    companion object {
        fun playerQuitEvent(player: Player) {
            EmpireUtils.EmpireRunnable {
                for (npc in abstractNPC)
                    npc.hideNPCForPlayer(player)
            }.runTaskAsynchronously()

        }

        fun playerMoveEvent(player: Player) {
            EmpireUtils.EmpireRunnable {
                for (npc in abstractNPC)
                    npc.trackPlayer(player)
            }.runTaskAsynchronously()
        }

        fun playerJoinEvent(player: Player) {
            EmpireUtils.EmpireRunnable {
                for (npc in abstractNPC)
                    npc.showNPCToPlayer(player)
            }.runTaskAsynchronously()
        }

        lateinit var fileManager: FileManager
        lateinit var npcConfig:NPCConfig
        lateinit var npcs:List<EmpireNPC>
        lateinit var abstractNPC: List<EmpireAbstractNPC>
        lateinit var abstractNPCByID:Map<Int,EmpireAbstractNPC>
    }
    private lateinit var eventManager:EventManager

    private fun onEnable() {
        fileManager = FileManager("config/npcs.yml")
        npcConfig = NPCConfig.new()!!

        val npcList = mutableListOf<EmpireNPC>()
        val abstractNpcList = mutableListOf<EmpireAbstractNPC>()
        val abstractNpcMap = mutableMapOf<Int,EmpireAbstractNPC>()

        for(npcID in fileManager.getConfig().getConfigurationSection("npcs")?.getKeys(false)?: listOf()) {
            val npc = EmpireNPC.new(npcID) ?: continue
            val abstractNPC = EmpireAbstractNPC(npc)
            abstractNPC.spawnNPC()
            abstractNpcMap[abstractNPC.id] = abstractNPC
            abstractNpcList.add(abstractNPC)
            npcList.add(npc)
        }
        abstractNPCByID = abstractNpcMap
        abstractNPC = abstractNpcList
        npcs = npcList

        eventManager = EventManager()


    }

    init {
        if (EmpirePlugin.instance.server.pluginManager.getPlugin("ProtocolLib") != null)
            onEnable()
    }

    fun onDisable() {
        for (npc in abstractNPC)
            npc.onDisable()
        eventManager.onDisable()
    }
}