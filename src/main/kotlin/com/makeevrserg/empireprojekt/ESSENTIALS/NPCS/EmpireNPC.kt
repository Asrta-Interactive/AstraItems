package com.makeevrserg.empireprojekt.ESSENTIALS.NPCS

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.network.PlayerConnection
import net.minecraft.world.entity.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import java.util.*


class EmpireNPC {

    lateinit var npc: EntityPlayer
    var npcArmorStands = mutableListOf<ArmorStand>()
    val name: String
        get() = npc.name
    val id: Int
        get() = npc.id

    val location: Location
        get() = Location(npc)

    private fun Location(npc: EntityPlayer): Location {
        return Location(npc.world.world, npc.locX(), npc.locY(), npc.locZ(), npc.xRot, npc.yRot)
    }

    public fun setLocation(l: Location) {
        npc.setLocation(l)
    }

    public fun Create(player: Player, id: String, skinName: String? = null) {
        val profile = GameProfile(UUID.randomUUID(), id)//No more than 16 chars
        setNPCSkin(player, true, skinName ?: "Notch", profile)
        spawnNPC(player.location, profile)
        saveLocation(player.location)
    }

    private fun setArmorStands(location: Location,list:List<String>){
        for (line in list){
            val armorStand = location.world!!.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand
            armorStand.customName = EmpireUtils.HEXPattern(line)
            armorStand.isInvisible = true
            armorStand.isCustomNameVisible = true
            npcArmorStands.add(armorStand)
        }
    }

    public fun Create(section: ConfigurationSection) {
        val id = section.name
        val location = section.getLocation("location") ?: return
        val profile = GameProfile(UUID.randomUUID(), id)//No more than 16 chars
        setNPCSkin(section.getConfigurationSection("skin"), profile)

        setArmorStands(location,section.getStringList("lines"))
        spawnNPC(location, profile)
    }

    fun delete(p: Player) {
        hideNPCFromOnlinePlayers()
        clearArmorStands()

    }

    private fun clearArmorStands() {
        for (stand in npcArmorStands)
            stand.remove()
    }
    private fun showArmorStand(player:Player) {
        val connection = player.connection()
        for (stand in npcArmorStands) {
            val entity = stand as CraftEntity

            connection.sendPacket(PacketPlayOutSpawnEntity(entity.handle))
        }
    }
    private fun hideArmorStands(player:Player) {
        val connection = player.connection()
        for (stand in npcArmorStands) {
            val entity = stand as CraftEntity

            connection.sendPacket(PacketPlayOutEntityDestroy(entity.handle.id))
        }
    }


    fun spawnNPC(location: Location, profile: GameProfile) {
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

    public fun showNPC(player: Player, npc: EntityPlayer) {
        val connection = player.connection()//WARNING handle.b==handle.playerConnection
        connection.sendPacket(
            PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                npc
            )
        )//WARNING EnumPlayerInfoAction.a==EnumPlayerInfoAction.ADD_PLAYER
        connection.sendPacket(PacketPlayOutNamedEntitySpawn(npc))
        showArmorStand(player)


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
        hideArmorStands(player)
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
        val oldStand = npcArmorStands.toList()
        npcArmorStands.clear()
        for (stand in oldStand){
            stand.teleport(p.location)
            npcArmorStands.add(stand)
        }
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
        connection.sendPacket(PacketPlayOutEntityHeadRotation(npc, newLoc.yaw.toAngle()))
    }

    fun showNPCToOnlinePlayers() {
        for (p in Bukkit.getOnlinePlayers())
            showNPC(p, npc)

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