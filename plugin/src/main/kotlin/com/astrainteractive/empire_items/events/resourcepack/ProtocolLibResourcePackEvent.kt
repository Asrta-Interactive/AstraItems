package com.astrainteractive.empire_items.events.resourcepack

import com.astrainteractive.empire_items.di.TranslationModule
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.events.EventListener
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import com.comphenix.protocol.wrappers.WrappedChatComponent
import ru.astrainteractive.astralibs.di.getValue

class ProtocolLibResourcePackEvent : EventListener {
    private val translations by TranslationModule
    private var protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()
    private var packetListener: PacketListener = object : PacketAdapter(
        AstraLibs.instance,
        ListenerPriority.HIGHEST,
        PacketType.Play.Server.RESOURCE_PACK_SEND,
    ) {
        override fun onPacketReceiving(event: PacketEvent) {
            val packet = event.packet
            //println("Packet Receiving: " + packet.type.name());
        }

        override fun onPacketSending(event: PacketEvent) {
            val packet = event.packet
            for (i in 0 until packet.chatComponents.size()) {
                val chatComponent = WrappedChatComponent.fromJson(translations.resourcePackMessage)
                packet.chatComponents.write(i, chatComponent)
            }
        }
    }

    init {
        protocolManager.addPacketListener(packetListener)
    }

    override fun onDisable() {
        protocolManager.removePacketListener(packetListener)
    }
}