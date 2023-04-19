package com.astrainteractive.empire_items.enchants.enchants

import com.astrainteractive.empire_items.enchants.core.EmpireEnchantEvent
import com.astrainteractive.empire_items.models.bukkit.EmpireEnchants
import com.atrainteractive.empire_items.models.enchants.EmpireEnchantsConfig
import com.atrainteractive.empire_items.models.enchants.GenericValueEnchant
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.persistence.Persistence.getPersistentData


class Vampirism(configModule: IDependency<EmpireEnchantsConfig>): EmpireEnchantEvent() {
    override val enchant = EmpireEnchants.VAMPIRISM
    override val enchantKey = "Вампиризм"
    private val config: EmpireEnchantsConfig by configModule
    override val materialWhitelist: List<Material>
        get() = listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.STONE_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.WOODEN_SWORD
        )
    override val empireEnchant: GenericValueEnchant = config.enchants.VAMPIRISM


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
