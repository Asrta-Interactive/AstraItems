package com.astrainteractive.empire_items.enchants

import com.astrainteractive.empire_items.enchants.core.AbstractPotionEnchant
import com.astrainteractive.empire_items.enchants.core.EmpireEnchantApi
import ru.astrainteractive.astralibs.events.EventListener
import ru.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.empire_items.enchants.enchants.*
import com.atrainteractive.empire_items.models.enchants.EmpireEnchantsConfig
import org.bukkit.Material
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import ru.astrainteractive.astralibs.di.IReloadable
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.BukkitConstant
import kotlin.random.Random

fun calcChance(chance: Int) = calcChance(chance.toDouble())
fun calcChance(chance: Double) = chance >= Random.nextDouble(0.0, 100.0)
fun calcChance(chance: Float) = calcChance(chance.toDouble())
class EnchantManager(
    private val configModule: IReloadable<EmpireEnchantsConfig>
) : EventManager {
    val config by configModule
    override val handlers: MutableList<EventListener> = mutableListOf()
    val EmpireEnchantsConfig.PotionEnchant.EnchantItemType.getList: List<Material>
        get() {
            return when (this) {
                EmpireEnchantsConfig.PotionEnchant.EnchantItemType.ARMOR -> EmpireEnchantApi.armorItems
                EmpireEnchantsConfig.PotionEnchant.EnchantItemType.SWORDS -> EmpireEnchantApi.swords
                EmpireEnchantsConfig.PotionEnchant.EnchantItemType.AXES -> EmpireEnchantApi.axes
                EmpireEnchantsConfig.PotionEnchant.EnchantItemType.PICKAXES -> EmpireEnchantApi.pickaxes
            }
        }

    init {
        FrostAspect(config).onEnable(this)
        Butcher(config).onEnable(this)
        Vampirism(config).onEnable(this)
        Vyderlight(config).onEnable(this)
        AquaLight(config).onEnable(this)
        AntiFall(config).onEnable(this)
        MegaJump(config).onEnable(this)
        config.potionEnchants.forEach { (key, it) ->
            val potionEffectType = PotionEffectType.getByName(it.effect) ?: return@forEach
            val enchant = BukkitConstant(it.id, PersistentDataType.INTEGER)
            object : AbstractPotionEnchant() {
                override val potionEffectType: PotionEffectType = potionEffectType
                override val enchant: BukkitConstant<Int, Int> = enchant
                override val enchantKey: String = it.id
                override val empireEnchant: EmpireEnchantsConfig.PotionEnchant = it
                override val materialWhitelist: List<Material>
                    get() = it.itemTypes.flatMap { it.getList }
            }.onEnable(this)
        }
    }
}