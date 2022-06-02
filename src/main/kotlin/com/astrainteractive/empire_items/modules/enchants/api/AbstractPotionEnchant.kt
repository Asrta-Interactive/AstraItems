package com.astrainteractive.empire_items.modules.enchants.api

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.empire_items.EmpirePlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


abstract class AbstractPotionEnchant : EmpireEnchantEvent() {
    override val materialWhitelist: List<Material>
        get() = EmpireEnchantApi.armorItems
    abstract val potionEffectType: PotionEffectType
    val executor = Bukkit.getScheduler().runTaskTimerAsynchronously(EmpirePlugin.instance, Runnable {
        Bukkit.getOnlinePlayers().forEach { player ->
            val eEnchant = empireEnchant ?: return@forEach
            val inv = player.inventory
            listOfNotNull(inv.helmet, inv.chestplate, inv.leggings, inv.boots,inv.itemInMainHand,inv.itemInOffHand).forEach items@{
                val level = getEnchantLevel(it) ?: return@items

                AsyncHelper.callSyncMethod {
                    player.addPotionEffect(
                        PotionEffect(
                            potionEffectType,
                            300,
                            (level * eEnchant.totalMultiplier).toInt() - 1,
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
