package com.makeevrserg.empireprojekt.npc

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.npc.commands.CommandManager
import com.makeevrserg.empireprojekt.npc.data.EmpireNPC
import com.makeevrserg.empireprojekt.npc.data.NPCConfig
import com.makeevrserg.empireprojekt.npc.interact.EventManager
import com.makeevrserg.empireprojekt.empirelibs.EmpireUtils
import com.makeevrserg.empireprojekt.empirelibs.FileManager
import com.makeevrserg.empireprojekt.empirelibs.runTaskAsynchronously
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import kotlin.math.abs

class NPCManager {
    companion object {

        fun playerQuitEvent(player: Player) {
            EmpireUtils.EmpireRunnable {
                for (npc in abstractNPCList)
                    npc.hideNPCForPlayer(player)
                playerSpawnedNpcs[player]?.clear() ?: return@EmpireRunnable
            }.runTaskAsynchronously()
        }

        private fun Player.hideNPC(npc: AbstractNPC) {
            playerSpawnedNpcs[this]?.remove(npc)
            npc.hideNPCForPlayer(this)
        }

        private fun Player.showNPC(npc: AbstractNPC) {
            if (playerSpawnedNpcs[this]!!.contains(npc))
                return
            playerSpawnedNpcs[this]?.add(npc)
            npc.showNPCToPlayer(this)
        }

        private fun Player.trackNPC(npc: AbstractNPC) {
            EmpireUtils.EmpireRunnable {
                npc.trackPlayer(this)
            }.runTaskAsynchronously()
        }


        fun playerMoveEvent(p: Player) {
            EmpireUtils.EmpireRunnable {
                for (npc in abstractNPCList) {
                    if (p.location.world != npc.location.world)
                        continue
                    val dist = p.location.distance(npc.location)
                    synchronized(this) {
                        when {
                            dist > npcConfig.radiusHide -> p.hideNPC(npc)
                            dist > npcConfig.radiusTrack -> p.showNPC(npc)
                            else -> {
                                p.showNPC(npc)
                                p.trackNPC(npc)
                            }
                        }
                    }
                }
            }.runTaskAsynchronously()
        }

        fun playerJoinEvent(player: Player) {
            EmpireUtils.EmpireRunnable {
                for (npc in abstractNPCList)
                    npc.showNPCToPlayer(player)
                playerSpawnedNpcs[player] = mutableSetOf()
            }.runTaskAsynchronously()
        }
        fun createNPC(id:String,location: Location){
            val npc = EmpireNPC(id=id,location = location)
            val abstractNPC = AbstractNPC(npc)
            abstractNPC.spawnNPC()
            empireNPCList.add(npc)
            abstractNPCList.add(abstractNPC)
            abstractNPCByID[abstractNPC.id] = abstractNPC
            abstractNPCByName[id] = abstractNPC
            saveNPC(npc)
        }
        fun saveNPC(npc:EmpireNPC){
            EmpireNPC.save(npc)
        }

        /**
         * File with config and NPCS
         */
        lateinit var fileManager: FileManager

        /**
         * Config class
         */
        lateinit var npcConfig: NPCConfig

        /**
         * List of all NPCS in config
         */
        val empireNPCList: MutableSet<EmpireNPC> = mutableSetOf()

        /**
         * List of Minecraft NPCS
         */
        val abstractNPCList: MutableSet<AbstractNPC> = mutableSetOf()

        /**
         * List of Minecraft NPCS by ID
         */
        val abstractNPCByID: MutableMap<Int, AbstractNPC> = mutableMapOf()

        /**
         * List of Minecraft NPCS by their Names
         */
        val abstractNPCByName: MutableMap<String, AbstractNPC> = mutableMapOf()

        /**
         * List of spawned NPCS, which in range of player
         */
        val playerSpawnedNpcs: MutableMap<Player, MutableSet<AbstractNPC>> = mutableMapOf()



    }

    private fun clearListAndMap(){
        empireNPCList.clear()
        abstractNPCList.clear()
        abstractNPCByID.clear()
        abstractNPCByName.clear()
        playerSpawnedNpcs.clear()
    }

    private lateinit var eventManager: EventManager

    private fun onEnable() {
        fileManager = FileManager("config/npcs.yml")
        npcConfig = NPCConfig.new()!!

        CommandManager()
        for (npcID in fileManager.getConfig().getConfigurationSection("npcs")?.getKeys(false) ?: listOf()) {
            val npc = EmpireNPC.new(npcID) ?: continue
            val abstractNPC = AbstractNPC(npc)
            abstractNPC.spawnNPC()

            abstractNPCByName[npcID] = abstractNPC
            abstractNPCByID[abstractNPC.id] = abstractNPC
            abstractNPCList.add(abstractNPC)
            empireNPCList.add(npc)
        }

        eventManager = EventManager()


    }

    init {
        if (EmpirePlugin.instance.server.pluginManager.getPlugin("ProtocolLib") != null)
            onEnable()

        for (player in Bukkit.getOnlinePlayers()) {
            playerSpawnedNpcs[player] = mutableSetOf()
        }
    }

    fun onDisable() {
        for (npc in abstractNPCList)
            npc.onDisable()
        eventManager.onDisable()
        clearListAndMap()
    }
}