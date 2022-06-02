package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantEvent
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent


class FrostAspect : EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.FROST_ASPECT
    override val enchantKey = "Заговор льда"
    override val materialWhitelist: List<Material>
        get() = listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.STONE_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.WOODEN_SWORD
        )


    @EventHandler
    private fun onEntityDamage(e: EntityDamageByEntityEvent) {
        val eEnchant = empireEnchant ?: return
        if (e.damager !is Player) return
        val p = e.damager as Player
        val level = getEnchantLevel(p.inventory.itemInMainHand) ?: return
        e.entity.freezeTicks += (eEnchant.value * eEnchant.increaseModifier * level).toInt() * 20;

    }

    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }

}
