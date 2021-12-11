package com.astrainteractive.empireprojekt.empire_items.events

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.Logger
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.*
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.util.Config
import com.astrainteractive.empireprojekt.empire_items.util.EmpireUtils


import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.annotations.NotNull

/**
 * todo переделать в PlaceholderAPI
 */
class FontProtocolLibEvent : IAstraListener {
    private lateinit var protocolManager: ProtocolManager
    private lateinit var packetListener: PacketListener
    private fun changePlayerTabName(player: Player?) {
        player ?: return
        var format: String = Config.tabPrefix + player.name
        if (EmpirePlugin.instance.server.pluginManager.getPlugin("placeholderapi") != null)
            format = PlaceholderAPI.setPlaceholders(player, format)

        format = AstraUtils.HEXPattern(format)
        format = EmpireUtils.emojiPattern(format)
        player.setPlayerListName(format)
    }

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        val player = e.player
        Bukkit.getScheduler().runTaskAsynchronously(EmpirePlugin.instance, Runnable {
            val p = player
            changePlayerTabName(p)
        })

    }


    private fun initPackerListener() {


        packetListener = object : PacketAdapter(
            EmpirePlugin.instance,
            ListenerPriority.HIGHEST,
            PacketType.Play.Server.SCOREBOARD_OBJECTIVE,
            PacketType.Play.Server.SCOREBOARD_TEAM,
            PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE,
            PacketType.Play.Server.SCOREBOARD_SCORE,
            PacketType.Play.Server.PLAYER_INFO,
            PacketType.Play.Server.SET_TITLE_TEXT,
            PacketType.Play.Server.SET_SUBTITLE_TEXT,
            PacketType.Play.Server.CHAT,
            PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER,
            PacketType.Play.Server.OPEN_WINDOW

        ) {
            override fun onPacketReceiving(event: PacketEvent) {
                val packet = event.packet
//                println("Packet Receiving: " + packet.getType().name());
            }

            override fun onPacketSending(event: PacketEvent) {
//                println("Packet Sending: " + event.packet.getType().name());
                fun chatCompToEmoji(packet: PacketContainer, i: Int) {
                    val chatComponent = packet.chatComponents.read(i) ?: return
                    chatComponent.json = EmpireUtils.emojiPattern(chatComponent.json)
                    //packet.chatComponents.setReadOnly(i, false)
                    packet.chatComponents.write(i, chatComponent)
                }

                fun convertTextComponent(textComponent: TextComponent): @NotNull Component {
                    val line = EmpireUtils.emojiPattern(GsonComponentSerializer.gson().serialize(textComponent))
                    return GsonComponentSerializer.gson().deserialize(line)
                }

                fun getListComponent(translatableComponent: TranslatableComponent): MutableList<Component> {
                    val listComponent = mutableListOf<Component>()
                    for (arg in translatableComponent.args()) {
                        val compo = convertTextComponent(arg as TextComponent)
                        listComponent.add(compo)
                    }
                    return listComponent
                }

                val packet = event.packet
                //println(packet.type)
                for (i in 0 until packet.chatComponents.size()) {
                    chatCompToEmoji(packet, i)
                    for (j in 0 until packet.modifier.size()) {
                        val obj = packet.modifier.read(j) ?: continue
                        //println(obj)
                        if (obj is TranslatableComponent)
                            packet.modifier.write(j, obj.args(getListComponent(obj)))

                        if (obj is TextComponent)
                            packet.modifier.write(j, convertTextComponent(obj))
                    }

                }
            }
        }
        protocolManager.addPacketListener(packetListener)

    }

    override fun onDisable() {

        protocolManager.removePacketListener(packetListener)
        PlayerJoinEvent.getHandlerList().unregister(this)

    }

    init {
        protocolManager = ProtocolLibrary.getProtocolManager()
        initPackerListener()
        for (player in Bukkit.getOnlinePlayers())
            changePlayerTabName(player)

    }
}