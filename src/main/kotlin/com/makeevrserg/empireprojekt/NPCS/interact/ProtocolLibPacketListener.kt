package com.makeevrserg.empireprojekt.NPCS.interact

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.*
import com.comphenix.protocol.wrappers.EnumWrappers
import com.makeevrserg.empireprojekt.NPCS.NPCManager
import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.Bukkit

class ProtocolLibPacketListener {

    private lateinit var protocolManager: ProtocolManager
    private lateinit var packetListener: PacketListener


    private fun initPackerListener() {
        packetListener = object : PacketAdapter(
            EmpirePlugin.instance,
            ListenerPriority.HIGHEST,
            PacketType.Play.Client.USE_ENTITY
        ) {
            override fun onPacketReceiving(event: PacketEvent) {
                val packet = event.packet
                val player = event.player
                for (i in 0 until packet.enumEntityUseActions.size()) {
                    if (!packet.enumEntityUseActions.read(i).action.toString().equals("INTERACT", ignoreCase = true))
                        continue
                    if (packet.enumEntityUseActions.read(i).hand == EnumWrappers.Hand.OFF_HAND)
                        continue
                    var npcID: Int? = null
                    for (j in 0 until packet.integers.size())
                        if (!packet.integers.getField(j).name.equals("a"))
                            continue
                        else
                            npcID = packet.integers.read(j)
                    npcID ?: return

                    for (npc in NPCManager.NPC)
                        if (npc.id == npcID)
                            Bukkit.getScheduler().scheduleSyncDelayedTask(EmpirePlugin.instance) {
                                EmpirePlugin.instance.server.pluginManager.callEvent(RightClickNPC(player, npc))
                            }

                }
            }

            override fun onPacketSending(event: PacketEvent) {
                //println("Packet Sending: " + event.packet.getType().name());
            }
        }
        protocolManager.addPacketListener(packetListener)
    }

    fun onDisable() {
        protocolManager.removePacketListener(packetListener)
    }

    init {
        EmpirePlugin.instance.server.pluginManager.getPlugin("protocollib")?.let {
            protocolManager = ProtocolLibrary.getProtocolManager()
            initPackerListener()
        }
    }
}