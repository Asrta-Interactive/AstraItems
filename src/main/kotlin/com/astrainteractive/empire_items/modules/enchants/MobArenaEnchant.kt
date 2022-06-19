package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.empire_items.util.playSound
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantEvent
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import com.astrainteractive.empire_items.modules.enchants.data.EmpireEnchantsConfig
import com.astrainteractive.empire_items.modules.enchants.data.enchants.GenericValueEnchant
import com.astrainteractive.empire_items.modules.enchants.data.enchants.SpawnMobArenaEnchant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent


class MobArenaEnchant : EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.MOB_ARENA_ENCHANT
    override val enchantKey = "MOB_ARENA_ENCHANT"
    override val materialWhitelist: List<Material>
        get() = listOf()
    override val empireEnchant: SpawnMobArenaEnchant = EmpireEnchantsConfig.enchants.MOB_ARENA_ENCHANT


    @EventHandler
    private fun interactEvent(e: PlayerInteractEvent) {
        getEnchantLevel(e.player.inventory.itemInMainHand) ?: return
        e.player.inventory.itemInMainHand.amount -= 1
        AsyncHelper.launch {
            AsyncHelper.callSyncMethod {
                empireEnchant.playCommand.forEach {
                    it.value.play(e.player)
                }
            }
            delay(100)
            empireEnchant.playSound.play(e.player.location)
            empireEnchant.playParticle.play(e.player.location)

        }
    }

    override fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

}
