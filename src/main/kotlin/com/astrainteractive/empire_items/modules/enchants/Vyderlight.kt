package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.empire_items.api.enchants.EmpireEnchantEvent
import com.astrainteractive.empire_items.api.enchants.EmpireEnchants
import com.astrainteractive.empire_items.api.enchants.models.EmpireEnchantsConfig
import com.astrainteractive.empire_items.api.enchants.models.GenericValueEnchant
import com.astrainteractive.empire_items.api.utils.calcChance
import org.bukkit.Material
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent


class Vyderlight : EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.VYDERLIGHT
    override val enchantKey = "Свет Богатсва"
    override val materialWhitelist: List<Material>
        get() = listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.STONE_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.WOODEN_SWORD
        )
    override val empireEnchant: GenericValueEnchant = EmpireEnchantsConfig.enchants.VYDERLIGHT


    @EventHandler
    private fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if (e.damager !is Player) return
        if (e.entity is Monster)
            return
        val p = e.damager as Player
        val level = getEnchantLevel(p.inventory.itemInMainHand) ?: return
        if (calcChance(20))
            p.damage(level * empireEnchant.value * e.damage)
        if (calcChance(20))
            p.location.world.strikeLightning(p.location)
        if (calcChance(10))
            e.damage = 0.0
    }

    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }

}
