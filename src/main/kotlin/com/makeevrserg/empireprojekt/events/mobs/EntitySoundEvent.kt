package com.makeevrserg.empireprojekt.events.mobs

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.*
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.potion.PotionEffectType
import org.jetbrains.annotations.NotNull

class EntitySoundEvent {

    private lateinit var protocolManager: ProtocolManager
    private lateinit var packetListener: PacketListener


    private fun initPackerListener() {


        packetListener = object : PacketAdapter(
            EmpirePlugin.instance,
            ListenerPriority.HIGHEST,
            PacketType.Play.Server.NAMED_SOUND_EFFECT

        ) {
            override fun onPacketReceiving(event: PacketEvent) {
//                val packet = event.packet
//                println("Packet Receiving: " + packet.type.name());
//                println(packet.strings)

            }

            override fun onPacketSending(event: PacketEvent) {
                return
                val packet = event.packet
//                println("Packet Sending: " + packet.type.name());
                val ints = packet.integers
                val w = event.player.world
                val x = ints.read(0) / 8.0
                val y = ints.read(1) / 8.0
                val z = ints.read(2) / 8.0
                val location = Location(w, x, y, z)
                var closestEntity: Entity? = null
                for (e in w.getNearbyEntities(location, 3.0, 3.0, 3.0))
                    if (closestEntity == null ||
                        e.location.distance(location) < closestEntity.location.distance(location)
                    )
                        closestEntity = e
                closestEntity ?: return

                if (closestEntity !is LivingEntity)
                    return
//                if ((closestEntity as LivingEntity).hasPotionEffect(PotionEffectType.INVISIBILITY))
//                    event.isCancelled = true

            }
        }
        protocolManager.addPacketListener(packetListener)

    }

    init {

        EmpirePlugin.instance.server.pluginManager.getPlugin("protocollib")?.let {
            protocolManager = ProtocolLibrary.getProtocolManager()
            initPackerListener()
        }
    }

    public fun onDisable() {
        protocolManager.removePacketListener(packetListener)
    }
}