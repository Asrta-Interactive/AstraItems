package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.empire_items.api.utils.BukkitConstants
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantEvent
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import org.bukkit.Material
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


class AquaLight : EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.AQUALIGHT
    override val enchantKey = "Свет Воды"
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
        if (e.entity is Monster)
            return
        val p = e.damager as Player
        val level = getEnchantLevel(p.inventory.itemInMainHand) ?: return

        p.remainingAir = -10
        p.damage(e.damage/level*eEnchant.totalMultiplier)
        p.addPotionEffect(
            PotionEffect(
                PotionEffectType.BLINDNESS,
                30,
                5,
                false,
                false,
                false
            )
        )
    }

    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }

}
