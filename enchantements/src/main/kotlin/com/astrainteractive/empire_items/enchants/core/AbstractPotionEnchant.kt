package com.astrainteractive.empire_items.enchants.core

import com.atrainteractive.empire_items.models.enchants.EmpireEnchantsConfig
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.async.BukkitMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


abstract class AbstractPotionEnchant : EmpireEnchantEvent() {
    override val materialWhitelist: List<Material>
        get() = EmpireEnchantApi.armorItems
    abstract val potionEffectType: PotionEffectType
    val executor = Bukkit.getScheduler().runTaskTimerAsynchronously(AstraLibs.instance, Runnable {
        Bukkit.getOnlinePlayers().forEach { player ->
            val eEnchant = empireEnchant as? EmpireEnchantsConfig.PotionEnchant?:run{
                Logger.error("Enchant ${enchantKey} is not PotionEnchant! Check yml config!")
                return@forEach
            }
            val inv = player.inventory
            listOfNotNull(inv.helmet, inv.chestplate, inv.leggings, inv.boots,inv.itemInMainHand,inv.itemInOffHand).forEach items@{
                val level = getEnchantLevel(it) ?: return@items

                PluginScope.launch(Dispatchers.BukkitMain) {
                    player.addPotionEffect(
                        PotionEffect(
                            potionEffectType,
                            300,
                            (level * eEnchant.value).toInt() - 1,
                            false,
                            false,
                            false
                        )
                    )
                }
            }
        }
    }, 0L, 40L)

    override fun onDisable() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
        executor.cancel()
    }

}
