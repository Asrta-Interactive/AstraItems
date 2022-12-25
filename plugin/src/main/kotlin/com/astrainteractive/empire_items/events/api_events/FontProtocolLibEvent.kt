package com.astrainteractive.empire_items.events.api_events

import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.utils.AstraPacketReader
import ru.astrainteractive.astralibs.utils.convertHex
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.*
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.di.configModule
import com.astrainteractive.empire_items.di.empireUtilsModule
import com.astrainteractive.empire_itemss.api.utils.EmpireUtils
import com.atrainteractive.empire_items.models.config.Config
import io.netty.channel.Channel


import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.network.protocol.Packet
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.di.getValue


/**
 * todo переделать в PlaceholderAPI
 */
class FontProtocolLibEvent() : EventListener {
    val empireUtils by empireUtilsModule
    private var protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()
    private lateinit var packetListener: PacketListener
    private val config by configModule

    companion object {
        private var instance: FontProtocolLibEvent? = null
    }

    private fun changePlayerTabName(player: Player?) {
        player ?: return
        var format: String = config.tabPrefix + player.name
        if (EmpirePlugin.instance.server.pluginManager.getPlugin("placeholderapi") != null)
            format = PlaceholderAPI.setPlaceholders(player, format)

        format = convertHex(format)
        format = empireUtils.emojiPattern(format)
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

    private fun <T : Packet<*>> createPacketListener(clazz: Class<out T>, block: (player: Player, packet: T) -> Unit) =
        object : AstraPacketReader<T>() {
            override val clazz: Class<out T> = clazz
            override val Player.provideChannel: Channel
                get() = (this as CraftPlayer).handle.b.b.m

            override fun readPacket(player: Player, packet: T) = block(player, packet)

        }

    private inline fun <reified T : Packet<*>> createPacketListener(noinline block: (player: Player, packet: T) -> Unit) =
        createPacketListener(T::class.java, block)

//    val ChatPacketListener = createPacketListener<PacketPlayInChat> { player, packet ->
//        val converted = EmpireUtils.emojiPattern(packet.b())
//        ReflectionUtil.setDeclaredField(PacketPlayInChat::class.java, packet, "b", converted)
//    }

    private fun initPackerListener() {
//        ChatPacketListener.onEnable()

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
            PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER,
            PacketType.Play.Server.OPEN_WINDOW

        ) {
            override fun onPacketReceiving(event: PacketEvent) {
            }

            override fun onPacketSending(event: PacketEvent) {
                fun chatCompToEmoji(packet: PacketContainer, i: Int) {
                    val chatComponent = packet.chatComponents.read(i) ?: return
                    chatComponent.json = empireUtils.emojiPattern(chatComponent.json)
                    packet.chatComponents.write(i, chatComponent)
                }

                fun convertTextComponent(textComponent: TextComponent): Component {
                    val line = empireUtils.emojiPattern(GsonComponentSerializer.gson().serialize(textComponent))
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
                for (i in 0 until packet.chatComponents.size()) {
                    chatCompToEmoji(packet, i)
                    for (j in 0 until packet.modifier.size()) {
                        val obj = packet.modifier.read(j) ?: continue
                        if (obj is TranslatableComponent)
                            packet.modifier.write(j, obj.args(getListComponent(obj)))

                        if (obj is TextComponent)
                            packet.modifier.write(j, convertTextComponent(obj))
                    }

                }
            }
        }
        protocolManager.addPacketListener(packetListener)
//        protocolManager.addPacketListener(testPacketListener)

    }

    override fun onDisable() {
//        catching(true) { ChatPacketListener.onDisable() }
        protocolManager.removePacketListener(packetListener)
//        protocolManager.removePacketListener(testPacketListener)
        PlayerJoinEvent.getHandlerList().unregister(this)
    }

    init {
        instance?.let { it.onDisable() }
        instance = this
        initPackerListener()
        for (player in Bukkit.getOnlinePlayers())
            changePlayerTabName(player)
    }
}