package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.empire_items.api.utils.getPersistentData
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantEvent
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import com.astrainteractive.empire_items.modules.enchants.data.EmpireEnchantsConfig
import com.astrainteractive.empire_items.modules.enchants.data.enchants.GenericValueEnchant
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent


class Vampirism : EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.VAMPIRISM
    override val enchantKey = "Вампиризм"
    override val materialWhitelist: List<Material>
        get() = listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.STONE_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.WOODEN_SWORD
        )
    override val empireEnchant: GenericValueEnchant = EmpireEnchantsConfig.enchants.VAMPIRISM


    @EventHandler
    private fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if (e.damager !is Player) return
        val p = e.damager as Player

        val itemStack = p.inventory.itemInMainHand
        val vampSize = itemStack.itemMeta?.getPersistentData(EmpireEnchants.VAMPIRISM) ?: return
        val toAddHealth: Double = e.finalDamage * vampSize * empireEnchant.value
        p.health = (toAddHealth + p.health).coerceAtMost(p.maxHealth)
    }

    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }

}
