package com.makeevrserg.empireprojekt.NPCS

import com.comphenix.protocol.ProtocolLibrary
import com.makeevrserg.empireprojekt.NPCS.interact.ClickNPC
import com.makeevrserg.empireprojekt.NPCS.interact.ProtocolLibPacketListener
import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class NPCManager {

    data class Config(
        val radiusTrack: Int = 20,
        val radiusHide: Int = 70,
        val npcRemoveListTime: Long = 200,
        val spawnNPCPacketTime: Long = 500,
        val npcTrackTime:Int = 200
    ) {
        constructor(section: ConfigurationSection?) : this(
            section?.getInt("radius_track",20)?:20,
            section?.getInt("radius_hide",70)?:70,
            section?.getLong("remove_list_time",200)?:200,
            section?.getLong("spawn_packet_time",500)?:500,
            section?.getInt("track_time",200)?:200,

        )
    }


    companion object {
        lateinit var instance: NPCManager
            private set
        var selectedNPC: MutableMap<Player, EmpireNPC> = mutableMapOf()
            private set
        var config: Config = Config()
            private set

        val protocolManager = ProtocolLibrary.getProtocolManager()

        public val playerSpawnedNPCS: MutableMap<Player, MutableList<EmpireNPC>> = mutableMapOf()


        public val NPC: MutableList<EmpireNPC> = mutableListOf()
        public val NPCMap: MutableMap<String, EmpireNPC> = mutableMapOf()

        public fun createNPC(player: Player, name: String, skin: String? = null) {
            val npc = EmpireNPC()
            npc.Create(player, name, skin)
            NPC.add(npc)
            NPCMap[name] = npc
        }

        private fun Player.hideNPC(npc: EmpireNPC) {
            playerSpawnedNPCS[this]?.remove(npc)
            npc.hideNPC(this, npc.npc)
        }

        private fun Player.showNPC(npc: EmpireNPC) {

            if (playerSpawnedNPCS[this]!!.contains(npc))
                return
            playerSpawnedNPCS[this]?.add(npc)

            npc.showNPC(this)
        }

        private fun Player.trackNPC(npc: EmpireNPC) {
            npc.trackPlayerNPC(this)
        }

        public fun playerJoinEvent(player: Player) {
            for (npc in NPC)
                npc.showNPC(player)
            playerSpawnedNPCS[player] = mutableListOf()
        }

        public fun playerQuitEvent(player: Player) {
            playerSpawnedNPCS[player]?.clear() ?: return
        }

        public fun playerMoveEvent(player: Player) {
            Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable {
                val p = player ?: return@Runnable
                for (npc in NPC) {
                    if (p.location.world != npc.location.world)
                        continue
                    val dist = p.location.distance(npc.location)
                    when {
                        dist > config.radiusHide -> p.hideNPC(npc)
                        dist > config.radiusTrack -> p.showNPC(npc)
                        else -> {
                            p.showNPC(npc)
                            p.trackNPC(npc)
                        }
                    }

                }
            })

        }

        public fun addNPC(npc: EmpireNPC) {
            NPCManager.NPC.add(npc)
            NPCManager.NPCMap[npc.name] = npc
        }

        public fun changeNPC(npc: EmpireNPC) {
            NPC[NPC.indexOf(npc)] = npc
            NPCManager.NPCMap[npc.name] = npc

        }

        public fun relocateNPC(player: Player, npcID: String? = null) {
            val npc = selectedNPC[player] ?: NPCMap[npcID] ?: return
            npc.relocateNPC(player)
        }

        public fun removeNPC(player: Player, npcID: String? = null) {
            val npc = selectedNPC[player] ?: NPCMap[npcID] ?: return
            NPC.removeAt(NPC.indexOf(npc))
            NPCMap.remove(npc.name)
            npc.hideNPCFromOnlinePlayers()
            EmpirePlugin.empireFiles.npcs.getConfig()!!.set("npcs.${npc.name}", null)
            EmpirePlugin.empireFiles.npcs.saveConfig()
        }

        public fun changeSkin(player: Player, skinName: String, npcID: String? = null) {
            val npc = selectedNPC[player] ?: NPCMap[npcID] ?: return
            npc.changeSkin(player, skinName)
        }

        public fun despawnNPCS() {
            val npcs = NPC.toList()
            NPC.clear()
            for (npc in npcs) {
                npc.hideNPCFromOnlinePlayers()
                NPC.remove(npc)
            }

        }

    }


    private fun loadNPCS() {
        val fileConfig = EmpirePlugin.empireFiles.npcs.getConfig()?.getConfigurationSection("npcs") ?: return
        config = Config(EmpirePlugin.empireFiles.npcs.getConfig()?.getConfigurationSection("config"))
        for (key in fileConfig.getKeys(false)) {
            val npc = EmpireNPC()
            npc.load(fileConfig.getConfigurationSection(key)!!)

            addNPC(npc)
        }

    }

    init {
        instance = this
        loadNPCS()
        for (player in Bukkit.getOnlinePlayers()) {
            playerSpawnedNPCS[player] = mutableListOf()
        }
    }


    private val clickNPC: ClickNPC = ClickNPC()
    private val protocolLibPacketListener = ProtocolLibPacketListener()
    private val npcCommandHandler: NPCCommandHandler = NPCCommandHandler()


    public fun onDisable() {
        despawnNPCS()
        clickNPC.onDisable()
        protocolLibPacketListener.onDisable()
    }
}