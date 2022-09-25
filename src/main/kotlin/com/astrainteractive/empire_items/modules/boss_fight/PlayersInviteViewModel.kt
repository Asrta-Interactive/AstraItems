package com.astrainteractive.empire_items.modules.boss_fight

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.BukkitMain
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.utils.HEX
import com.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_items.api.models.CONFIG
import com.astrainteractive.empire_items.api.models.yml_item.Interact
import com.astrainteractive.empire_items.empire_items.commands.CommandManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
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
//        var customEntityInfo: CustomEntityInfo? = null
        var executor: String? = null
        var currentTeam = mutableSetOf<Player>()
        fun canTeleport(player: Player): Boolean {
            val isOnline = Bukkit.getOnlinePlayers().firstOrNull { it.name.equals(executor, ignoreCase = true) } != null
            if (initializing) return false
            if (!isOnline) return true
            return false
//            return customEntityInfo == null || customEntityInfo?.entity?.isDead == true
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
        currentTeam.addAll(players)

        initializing = true
        executor = playerMenuUtility.player.name
        players.forEach {
            it.sendMessage("&2 Через &6${CONFIG.arenaCommand.playersTeleportDelay / 1000} &2секунд вас телепортирует к боссу".HEX())
            Interact.PlaySound("minecraft:block.bell.use", 1f, 10f).play(it.location)
        }
        AsyncHelper.launch {
            delay(CONFIG.arenaCommand.playersTeleportDelay)
            AsyncHelper.launch(Dispatchers.BukkitMain) {
                players.forEach {
                    it.teleport(CONFIG.arenaCommand.bossLocation.toBukkitLocation())
                }
            }
        }
        AsyncHelper.launch {
            delay(CONFIG.arenaCommand.bossSpawnDelay)
            val mob = EmpireItemsAPI.ymlMobById[CONFIG.arenaCommand.mobID]!!
            AsyncHelper.launch(Dispatchers.BukkitMain) {
//                customEntityInfo = ModelEngineApi.spawnMob(mob, CONFIG.arenaCommand.bossLocation.toBukkitLocation())
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
        meta.owningPlayer = Bukkit.getOfflinePlayer(player.uniqueId)
        item.itemMeta = meta
        return item
    }
}

