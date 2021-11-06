package com.astrainteractive.empireprojekt.essentials.sit

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.EmpirePlugin.Companion.translations
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.spigotmc.event.entity.EntityDismountEvent

class SitEvent : IAstraListener {


    private val sitPlayers = mutableMapOf<Player, ArmorStand>()

    /**
     * Заставляет игрока сесть
     */
    fun sitPlayer(player: Player, loc: Location? = null) {
        val location = loc ?: player.location
        //Сидит ли уже игрок
        if (sitPlayers.contains(player)) {
            player.sendMessage(translations.SIT_ALREADY)
            return
        }
        //Находится ли игрок в воздухе
        if (player.isFlying) {
            player.sendMessage(translations.SIT_IN_AIR)
            return
        }
        //Находится ли игрок в воздухе
        if (player.location.block.getRelative(BlockFace.DOWN).type == Material.AIR) {
            player.sendMessage(translations.SIT_IN_AIR)
            return
        }
        //Создаем стул
        val chair = location.world?.spawnEntity(location.add(0.0, -1.6, 0.0), EntityType.ARMOR_STAND) as ArmorStand
        chair.setGravity(false)
        chair.isVisible = false
        chair.isInvulnerable = false
        //Садим игрока
        chair.addPassenger(player)
        //Добавялем игрока в список посаженных
        sitPlayers[player] = chair


    }

    /**
     * Функция заставляет игрока встать
     */
    private fun stopSitPlayer(player: Player) {
        //Берем текущий стул игрока
        val armorStand = sitPlayers[player] ?: return
        //Удаляем стул и убираем игрока из списка
        armorStand.remove()
        sitPlayers.remove(player)
        //Телепортируем чуть повыше
        player.teleport(player.location.add(0.0,1.6,0.0))
    }
    @EventHandler
    fun onDeathEvent(e:PlayerDeathEvent){
        stopSitPlayer(e.entity)
    }

    @EventHandler
    fun onTeleportEvent(e:PlayerTeleportEvent){
        stopSitPlayer(e.player)
    }


    @EventHandler
    fun playerInteractEvent(e: PlayerInteractEvent) {
        if (e.action!=Action.RIGHT_CLICK_BLOCK)
            return
    }

    @EventHandler
    fun onDisconnect(e: PlayerQuitEvent) {
        stopSitPlayer(e.player)
    }


    @EventHandler
    fun onDismount(e: EntityDismountEvent) {
        if (e.entity !is Player)
            return
        stopSitPlayer(e.entity as Player)
    }

    companion object {
        lateinit var instance: SitEvent
            private set
    }

    init {
        instance = this
    }


    override fun onDisable() {
        for (player in sitPlayers.keys)
            sitPlayers[player]!!.remove()
        sitPlayers.clear()
        EntityDismountEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
        PlayerTeleportEvent.getHandlerList().unregister(this)
    }

}