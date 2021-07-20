package com.makeevrserg.empireprojekt.NPCS

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.core.BaseBlockPosition
import net.minecraft.core.BlockPosition
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.network.PlayerConnection
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.decoration.EntityArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import java.util.*


class EmpireNPC {

    data class Command(val command: String, val as_console: Boolean)

    lateinit var npc: EntityPlayer
    var npcArmorStands = mutableListOf<EntityArmorStand>()
    val name: String
        get() = npc.name
    val id: Int
        get() = npc.id
    val location: Location
        get() = Location(npc)
    var phrases: List<String> = mutableListOf()
    var commands: MutableList<Command> = mutableListOf()


    private fun Location(npc: EntityPlayer): Location {
        return Location(npc.world.world, npc.locX(), npc.locY(), npc.locZ(), npc.xRot, npc.yRot)
    }
    public fun Create(player: Player, id: String, skinName: String? = null) {
        val profile = GameProfile(UUID.randomUUID(), id)//No more than 16 chars
        setNPCSkin(player, true, skinName ?: "Notch", profile)
        spawnNPC(player.location, profile)
        saveLocation(player.location)
    }

    private fun setArmorStands(location: Location, list: List<String>) {
        var amount = 0.0
        for (line in list) {
            val armorStand = EntityArmorStand(EntityTypes.c, (location.world as CraftWorld).handle.minecraftWorld)
            armorStand.setLocation(
                location.x,
                location.y+amount,
                location.z,
                location.yaw,
                location.pitch
            )
            amount+=0.2
            armorStand.customName = CraftChatMessage.fromStringOrNull(EmpireUtils.HEXPattern(line))
            armorStand.customNameVisible = true
            armorStand.isInvisible = true
            armorStand.setArms(true)
            npcArmorStands.add(armorStand)
        }
    }

    public fun load(section: ConfigurationSection) {
        val id = section.name
        val location = section.getLocation("location") ?: return
        val profile = GameProfile(UUID.randomUUID(), id)//No more than 16 chars
        phrases = EmpireUtils.HEXPattern(section.getStringList("phrases"))
        setNPCSkin(section.getConfigurationSection("skin"), profile)
        for (cmdKey in section.getConfigurationSection("commands")?.getKeys(false) ?: mutableSetOf())
            commands.add(
                Command(
                    section.getString("commands.$cmdKey.command") ?: continue,
                    section.getBoolean("commands.$cmdKey.as_console")
                )
            )

        setArmorStands(location, section.getStringList("lines"))
        spawnNPC(location, profile)
    }

    fun delete(p: Player) {
        hideNPCFromOnlinePlayers()
        clearArmorStands()

    }

    private fun clearArmorStands() {
        for (stand in npcArmorStands)
            stand.killEntity()
    }
    private fun showArmorStandsToPlayer(player: Player) {
        val connection = player.connection()
        for (stand in npcArmorStands) {
            val packetPlayOutSpawnEntity = PacketPlayOutSpawnEntity(stand);
            val metadata = PacketPlayOutEntityMetadata(stand.id, stand.dataWatcher, true);
            connection.sendPacket(packetPlayOutSpawnEntity)
            connection.sendPacket(metadata)
        }
    }

    private fun hideArmorStandsForPlayer(player: Player) {
        val connection = player.connection()
        for (stand in npcArmorStands) {
            connection.sendPacket(PacketPlayOutEntityDestroy(stand.id))
        }
    }


    private fun spawnNPC(location: Location, profile: GameProfile) {
        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        val world = (location.world as CraftWorld).handle
        npc = EntityPlayer(server, world, profile)
        npc.setLocation(location)
        npc.listName = IChatBaseComponent.a("")

        initScoreboard()
        showNPCToOnlinePlayers()
        setInvisibleName()
    }

    private fun saveLocation(location: Location) {
        EmpirePlugin.empireFiles.npcs.getConfig()!!.set("npcs.${npc.name}.location", location)
        EmpirePlugin.empireFiles.npcs.saveConfig()
    }

    private fun EntityPlayer.setLocation(l: Location) {
        this.setLocation(l.x, l.y, l.z, l.yaw, l.pitch)
    }

    private fun Player.connection(): PlayerConnection {
        return (this as CraftPlayer).handle.b
    }



    private val playerSpawnedList:MutableList<PlayerConnection> = mutableListOf()
    private fun spawnNPCPacket(connection: PlayerConnection){
//        if (playerSpawnedList.contains(connection))
//            return
//        playerSpawnedList.add(connection)
        connection.sendPacket(
            PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                npc
            )
        )//WARNING EnumPlayerInfoAction.a==EnumPlayerInfoAction.ADD_PLAYER
        Bukkit.getScheduler().runTaskLaterAsynchronously(EmpirePlugin.instance,
            Runnable {
                connection.sendPacket(
                    PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
                        npc
                    ))
//                playerSpawnedList.remove(connection)
            },NPCManager.config.npcRemoveListTime)
    }

    public fun showNPC(player: Player) {
        val connection = player.connection()//WARNING handle.b==handle.playerConnection
        spawnNPCPacket(connection)
        connection.sendPacket(PacketPlayOutNamedEntitySpawn(npc))
        showArmorStandsToPlayer(player)



    }

    public fun hideNPC(player: Player, npc: EntityPlayer) {
        val connection = player.connection()

        connection.sendPacket(
            PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
                npc
            )
        )//WARNING EnumPlayerInfoAction.e==EnumPlayerInfoAction.REMOVE_PLAYER
        connection.sendPacket(PacketPlayOutEntityDestroy(npc.id))
        hideArmorStandsForPlayer(player)
    }

    private fun Float.toAngle(): Byte {
        return (this * 256 / 360).toInt().toByte()
    }


    fun setNPCSkin(section: ConfigurationSection?, profile: GameProfile) {
        section ?: return
        profile.properties.put(
            "textures",
            Property("textures", section.getString("texture") ?: return, section.getString("signature") ?: return)
        )
    }

    fun setNPCSkin(
        player: Player? = null,
        saveConfig: Boolean = false,
        playerName: String,
        profile: GameProfile
    ): Boolean {
        val name = EmpireUtils.getSkinByPlayerName(player, playerName) ?: return false
        profile.properties.put("textures", Property("textures", name[0], name[1]))
        if (saveConfig) {
            println("Saveiung ${profile.name}")
            EmpirePlugin.empireFiles.npcs.getConfig()!!.set("npcs.${profile.name}.skin.texture", name[0])
            EmpirePlugin.empireFiles.npcs.getConfig()!!.set("npcs.${profile.name}.skin.signature", name[1])
            EmpirePlugin.empireFiles.npcs.saveConfig()
        }
        return true
    }


    fun changeSkin(p: Player, playerName: String) {
        hideNPCFromOnlinePlayers()
        setNPCSkin(p, saveConfig = true, playerName, npc.profile)
        showNPCToOnlinePlayers()
    }

    fun relocateNPC(p: Player) {
        hideNPCFromOnlinePlayers()
        npc.setLocation(p.location)
        NPCManager.changeNPC(this)
        showNPCToOnlinePlayers()
        saveLocation(p.location)
    }

    public fun trackPlayerNPC(player: Player) {
        val connection = player.connection()
        val npcLoc = npc.bukkitEntity.location
        val newLoc = npcLoc.setDirection(player.location.subtract(npcLoc).toVector())
        connection.sendPacket(
            PacketPlayOutEntity.PacketPlayOutEntityLook(
                npc.id,
                newLoc.yaw.toAngle(),
                newLoc.pitch.toAngle(),
                false
            )
        )
        Bukkit.getScheduler().runTaskLaterAsynchronously(EmpirePlugin.instance,
            Runnable {
                spawnNPCPacket(connection)
            },NPCManager.config.spawnNPCPacketTime)
        connection.sendPacket(PacketPlayOutEntityHeadRotation(npc, newLoc.yaw.toAngle()))
    }

    fun showNPCToOnlinePlayers() {
        for (p in Bukkit.getOnlinePlayers())
            showNPC(p)

    }

    fun hideNPCFromOnlinePlayers() {
        for (p in Bukkit.getOnlinePlayers())
            hideNPC(p, npc)

        clearArmorStands()
    }

    private fun setInvisibleName() {
        for (team in Bukkit.getScoreboardManager()?.mainScoreboard?.teams ?: return)
            team.removeEntry(this.name)

        scoreboardHideNameTeam?.addEntry(this.name)
    }

    var scoreboardHideNameTeam: Team? = null
    private fun initScoreboard() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
        scoreboard?.getTeam(name)?.unregister()
        scoreboardHideNameTeam = scoreboard?.registerNewTeam(name) ?: return
        scoreboardHideNameTeam!!.nameTagVisibility = NameTagVisibility.NEVER
    }
}