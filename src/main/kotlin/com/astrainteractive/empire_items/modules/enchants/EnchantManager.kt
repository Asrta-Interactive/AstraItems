package com.astrainteractive.empire_items.modules.enchants

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.empire_items.api.utils.BukkitConstant
import com.astrainteractive.empire_items.modules.enchants.api.AbstractPotionEnchant
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchantApi
import com.astrainteractive.empire_items.modules.enchants.api.EmpireEnchants
import kotlinx.coroutines.runBlocking
import org.bukkit.Material
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType


class EnchantManager : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()

    init {
        listOf(1, 2)
        runBlocking { EmpireEnchantApi.onEnable() }
        FrostAspect().onEnable(this)
        Butcher().onEnable(this)
        Vampirism().onEnable(this)
        Vyderlight().onEnable(this)
        AquaLight().onEnable(this)
        AntiFall().onEnable(this)
        EmpireEnchantApi.potionEffectEnchants.forEach {
            val potionEffectType = PotionEffectType.getByName(it.potionEffectType) ?: return@forEach
            val enchant = BukkitConstant(it.id, PersistentDataType.INTEGER)
            object : AbstractPotionEnchant() {
                override val potionEffectType: PotionEffectType = potionEffectType
                override val enchant: BukkitConstant<Int, Int> = enchant
                override val enchantKey: String = it.id
                override val materialWhitelist: List<Material>
                    get() = it.itemTypes.flatMap { it.getList }
            }.onEnable(this)
        }
    }

    override fun onDisable() {
        super.onDisable()
        runBlocking { EmpireEnchantApi.onDisable() }
    }
}