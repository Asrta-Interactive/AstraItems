package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantApi
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantEvent
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


class AntiFall : EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.ANTI_FALL
    override val enchantKey = "Гравитин"
    override val materialWhitelist: List<Material>
        get() = EmpireEnchantApi.armorItems

    @EventHandler
    private fun onEntityFall(e: EntityDamageEvent) {
        if (e.cause != EntityDamageEvent.DamageCause.FALL) return
        val eEnchant = empireEnchant ?: kotlin.run {
            Logger.error("Not found empireEnchant ${enchantKey}")
            return
        }
        val player = (e.entity as? Player) ?: return
        val list = player.inventory.armorContents?.mapNotNull {
            it ?: return@mapNotNull null
            getEnchantLevel(it)
        }
        if (list.isNullOrEmpty()) return
        list.forEach {
            val total = if (eEnchant.totalMultiplier < 1) 1 / it else it
            e.damage *= eEnchant.totalMultiplier * total
        }

    }

    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }

}
