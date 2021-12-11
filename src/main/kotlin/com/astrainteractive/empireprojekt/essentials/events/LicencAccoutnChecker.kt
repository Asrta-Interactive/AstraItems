package com.astrainteractive.empireprojekt.essentials.events

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import com.comphenix.protocol.wrappers.WrappedChatComponent

class LicencAccoutnChecker:IAstraListener {


    private val TAG: String = this.javaClass.name

    private var protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()
    private var packetListener: PacketListener = object : PacketAdapter(
        AstraLibs.instance,
        ListenerPriority.HIGHEST,
        PacketType.Play.Server.LOGIN,
    ) {
        override fun onPacketReceiving(event: PacketEvent) {
            val packet = event.packet
            Logger.log("onPacketReceiving","${event.packetType.name()}")
        }

        override fun onPacketSending(event: PacketEvent) {
            val packet = event.packet
            Logger.log("onPacketSending","${event.packetType.name()}")
            println(packet.strings)


        }
    }

    init {
        protocolManager.addPacketListener(packetListener)
    }

    override fun onDisable() {
        protocolManager.removePacketListener(packetListener)
    }

}
