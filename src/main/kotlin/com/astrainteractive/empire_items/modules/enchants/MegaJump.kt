package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.valueOfOrNull
import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantApi
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantEvent
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import com.astrainteractive.empire_items.modules.enchants.data.EmpireEnchantsConfig
import com.astrainteractive.empire_items.modules.enchants.data.GenericEnchant
import com.astrainteractive.empire_items.modules.enchants.data.enchants.GenericValueEnchant
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


class MegaJump : EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.MEGA_JUMP
    override val enchantKey = "Прыжок"
    override val materialWhitelist: List<Material>
        get() = EmpireEnchantApi.armorItems
    override val empireEnchant: GenericValueEnchant = EmpireEnchantsConfig.enchants.MEGA_JUMP

    @EventHandler
    fun onJump(e: PlayerJumpEvent){

        val sum = e.player.inventory.armorContents?.mapNotNull {
            it?.let { getEnchantLevel(it) }?.times(empireEnchant.value)
        }?.sum()?:return
        AsyncHelper.launch {
            val total = empireEnchant.value
            if (e.player.isSneaking)
                e.player.velocity = e.to.subtract(e.from).toVector().multiply(sum)
        }
    }
    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }

}
