package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.empire_items.api.enchants.EmpireEnchantApi
import com.astrainteractive.empire_items.api.enchants.EmpireEnchantEvent
import com.astrainteractive.empire_items.api.enchants.EmpireEnchants
import com.astrainteractive.empire_items.api.enchants.models.EmpireEnchantsConfig
import com.astrainteractive.empire_items.api.enchants.models.GenericValueEnchant
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent


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
