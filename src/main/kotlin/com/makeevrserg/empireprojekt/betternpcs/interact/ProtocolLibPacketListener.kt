package com.makeevrserg.empireprojekt.betternpcs.interact

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.*
import com.comphenix.protocol.wrappers.EnumWrappers
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.betternpcs.BetterNPCManager
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.Bukkit

class ProtocolLibPacketListener:IEmpireListener {

    private lateinit var protocolManager: ProtocolManager
    private lateinit var packetListener: PacketListener


    private fun initPackerListener() {
        packetListener = object : PacketAdapter(
            EmpirePlugin.instance,
            ListenerPriority.NORMAL,
            PacketType.Play.Client.USE_ENTITY
        ) {
            override fun onPacketReceiving(event: PacketEvent) {
                val packet = event.packet
                val player = event.player

                Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable{
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
                        npcID ?: continue

                        Bukkit.getScheduler().scheduleSyncDelayedTask(EmpirePlugin.instance) {
                            EmpirePlugin.instance.server.pluginManager.callEvent(
                                RightClickNPC(
                                    player,
                                    BetterNPCManager.abstractNPCByID[npcID] ?: return@scheduleSyncDelayedTask
                                )
                            )
                        }

                    }
                })

            }

            override fun onPacketSending(event: PacketEvent) {
                //println("Packet Sending: " + event.packet.getType().name());
            }
        }
        protocolManager.addPacketListener(packetListener)
    }

    override fun onDisable() {
        protocolManager.removePacketListener(packetListener)
    }

    init {
        EmpirePlugin.instance.server.pluginManager.getPlugin("protocollib")?.let {
            protocolManager = ProtocolLibrary.getProtocolManager()
            initPackerListener()
        }
    }
}