package com.astrainteractive.empire_items.modules.boss_fight

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.api.mobs.CustomEntityInfo
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.empire_items.commands.CommandManager
import com.astrainteractive.empire_items.models.CONFIG
import com.astrainteractive.empire_items.models.yml_item.Interact
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.InputStreamReader
import java.net.URL
import java.util.*


data class PlayerInvite(
    val player: Player,
    val invited: Boolean = false,
    val accepted: Boolean = false,
    val skull: ItemStack
)


fun CommandManager.inviteAcceptCommand() = AstraLibs.registerCommand("emaccept") { sender, args ->
    if (sender !is Player) return@registerCommand
    val player = args.getOrNull(0)?.let { Bukkit.getPlayer(it) } ?: kotlin.run {
        sender.sendMessage("#fc1c03Такого игрока нет".HEX())
        return@registerCommand
    }
    PlayersInviteViewModel.onInviteAccepted(player, sender)
}

class PlayersInviteViewModel(val playerMenuUtility: AstraPlayerMenuUtility) {

    companion object {
        private val _instances = mutableSetOf<PlayersInviteViewModel>()
        fun onInviteAccepted(player: Player, sender: Player) {
            println(_instances.map { it.playerMenuUtility.player.name })
            val instance = _instances.firstOrNull { it.playerMenuUtility.player.name == player.name }
            if (instance == null) {
                sender.sendMessage("&4От этого игрока не было приглашения".HEX())
                return
            }
            instance.onInviteAccepted(player = sender)
        }

        private var initializing: Boolean = false
        var customEntityInfo: CustomEntityInfo? = null
        var executor: String? = null
        var currentTeam = mutableListOf<Player>()
        fun canTeleport(player: Player): Boolean {
            val isOnline = Bukkit.getOnlinePlayers().firstOrNull { it.name.equals(executor, ignoreCase = true) } != null
            if (initializing) return false
            if (!isOnline) return true
            return executor == player.name || customEntityInfo == null || customEntityInfo?.entity?.isDead == true
        }

    }

    private val onlinePlayers = Bukkit.getOnlinePlayers()
    private val skinByPlayer = runBlocking {
        withContext(AsyncHelper.coroutineContext) {
            onlinePlayers.associate {
                it.name to getHead(it)
            }
        }
    }

    private val _players =
        MutableStateFlow(onlinePlayers.filter { it != playerMenuUtility.player }
            .map { PlayerInvite(it, skull = skinByPlayer[it.name]!!) })
    val players: StateFlow<List<PlayerInvite>>
        get() = _players
    val ready: Boolean
        get() = players.value.filter { it.invited == it.accepted }.size == players.value.size

    fun onPlayerClicked(slot: Int) {
        val list = players.value.toMutableList()
        val playerInvite = list[slot]
        list[slot] = playerInvite.copy(invited = !playerInvite.invited)
        _players.value = list.toList()
        if (!playerInvite.invited) playerInvite.player.sendMessage("&2Игрок &6${playerMenuUtility.player.name} &2пригласил вас принять участие в бою. Чтобы принять приглашение введите &b/emaccept ${playerMenuUtility.player.name}".HEX())
    }

    fun onInviteAccepted(player: Player) {
        val list = players.value.toMutableList()
        val playerInvite = list.firstOrNull { it.player == player } ?: return
        if (!playerInvite.invited) {
            player.sendMessage("&2Вас не пригласили на этот бой".HEX())
            return
        }

        player.sendMessage("&2Вы приняли приглашение на бой".HEX())
        val index = list.indexOf(playerInvite)
        list[index] = playerInvite.copy(accepted = true)
        _players.value = list.toList()
        playerMenuUtility.player.sendMessage("&2Игрок &6${player.name} &2принял приглашение на бой".HEX())
    }

    fun onReadyClicked() {
        val players = players.value.filter { it.invited && it.accepted }.map { it.player }.toMutableList().apply {
            add(playerMenuUtility.player)
        }
        if (!ready) return
        if (!canTeleport(playerMenuUtility.player)) {
            playerMenuUtility.player.sendMessage("#fc1c03Босс уже на арене. Телепортироваться может только ${PlayersInviteViewModel.executor}")
            return
        }
        val secretItem =
            playerMenuUtility.player.inventory.contents?.firstOrNull { it?.empireID == CONFIG.arenaCommand.itemID }
        if (secretItem == null) {
            playerMenuUtility.player.sendMessage("#fc1c03Нет предмета в руке ${CONFIG.arenaCommand.itemID}".HEX())
            return
        }
        secretItem.amount -= 1

        initializing = true
        executor = playerMenuUtility.player.name
        players.forEach {
            it.sendMessage("&2 Через &6${CONFIG.arenaCommand.playersTeleportDelay / 1000} &2секунд вас телепортирует к боссу".HEX())
            Interact.PlaySound("minecraft:block.bell.use", 1f, 10f).play(it.location)
        }
        AsyncHelper.launch {
            delay(CONFIG.arenaCommand.playersTeleportDelay)
            AsyncHelper.callSyncMethod {
                players.forEach {
                    it.teleport(CONFIG.arenaCommand.bossLocation.toBukkitLocation())
                }
            }
        }
        AsyncHelper.launch {
            delay(CONFIG.arenaCommand.bossSpawnDelay)
            val mob = EmpireItemsAPI.ymlMobById[CONFIG.arenaCommand.mobID]!!
            AsyncHelper.callSyncMethod {
                if (customEntityInfo != null) return@callSyncMethod
                customEntityInfo = MobApi.spawnMob(mob, CONFIG.arenaCommand.bossLocation.toBukkitLocation())
            }
        }
        AsyncHelper.launch {
            delay(CONFIG.arenaCommand.bossSpawnDelay + CONFIG.arenaCommand.playersTeleportDelay)
            initializing = false
        }
    }

    init {
        _instances.add(this)
    }

    fun onDestroy() {
        _instances.remove(this)
    }

    suspend fun getHead(player: Player): ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta: SkullMeta = item.itemMeta as SkullMeta
        val profile = GameProfile(UUID.randomUUID(), null)
        val skin = getSkinByName(player.name)
        profile.properties.put("textures", Property("textures", skin?.first, skin?.second))
        BlockParser.setDeclaredField(meta::class.java, meta, "profile", profile)
        item.itemMeta = meta
        return item
    }

    private suspend fun getSkinByName(name: String) = catching {
        val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
        val reader = InputStreamReader(url.openStream())
        val uuid = JsonParser().parse(reader).asJsonObject.get("id").asString
        val url2 = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
        val reader2 = InputStreamReader(url2.openStream())
        val property =
            JsonParser().parse(reader2).asJsonObject.get("properties").asJsonArray.get(0).asJsonObject
        val value = property.get("value").asString
        val signature = property.get("signature").asString
        value to signature
    }
}

