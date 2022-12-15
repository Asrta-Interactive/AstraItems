package com.astrainteractive.empire_items.events.api_events

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.AstraLibs

abstract class ProtocolLibListener(
    private val priority: ListenerPriority = ListenerPriority.HIGHEST,
    vararg val types: PacketType
) {

    private var protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()
    private var packetListener: PacketListener? = null
    fun onEnable() {
        packetListener = object : PacketAdapter(
            AstraLibs.instance,
            priority,
            *types
        ) {
            override fun onPacketReceiving(event: PacketEvent?) {
                event?.let(this@ProtocolLibListener::onPacketReceiving)
            }

            override fun onPacketSending(event: PacketEvent?) {
                event?.let(this@ProtocolLibListener::onPacketSending)

            }
        }
        protocolManager.addPacketListener(packetListener)
    }

    abstract fun onPacketReceiving(event: PacketEvent)
    abstract fun onPacketSending(event: PacketEvent)

    fun onDisable() {
        packetListener?.let(protocolManager::removePacketListener)
    }

}