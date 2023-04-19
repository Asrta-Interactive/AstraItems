package com.astrainteractive.empire_items.enchants.enchants

import com.astrainteractive.empire_items.enchants.core.EmpireEnchantApi
import com.astrainteractive.empire_items.enchants.core.EmpireEnchantEvent
import com.astrainteractive.empire_items.models.bukkit.EmpireEnchants
import com.atrainteractive.empire_items.models.enchants.EmpireEnchantsConfig
import com.atrainteractive.empire_items.models.enchants.GenericValueEnchant
import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue


class MegaJump(configModule: IDependency<EmpireEnchantsConfig>): EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.MEGA_JUMP
    override val enchantKey = "Прыжок"
    private val config: EmpireEnchantsConfig by configModule
    override val materialWhitelist: List<Material>
        get() = EmpireEnchantApi.armorItems
    override val empireEnchant: GenericValueEnchant = config.enchants.MEGA_JUMP

    @EventHandler
    fun onJump(e: PlayerJumpEvent){

        val sum = e.player.inventory.armorContents?.mapNotNull {
            it?.let { getEnchantLevel(it) }?.times(empireEnchant.value)
        }?.sum()?:return
        val total = empireEnchant.value
        if (e.player.isSneaking)
            e.player.velocity = e.to.subtract(e.from).toVector().multiply(sum)
    }
    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }

}
