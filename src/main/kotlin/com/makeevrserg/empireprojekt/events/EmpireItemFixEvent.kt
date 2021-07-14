package com.makeevrserg.empireprojekt.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class EmpireItemFixEvent : Listener {

    private fun replaceWrongItem(toReplace: ItemStack?): ItemStack? {
        toReplace?:return null
        val meta = toReplace.itemMeta ?: return toReplace
        val id = EmpireUtils.getEmpireID(toReplace) ?: return toReplace
        if (meta.persistentDataContainer.has(EmpirePlugin.empireConstants.FIXED_ITEM, PersistentDataType.SHORT))
            return toReplace


        val item = EmpirePlugin.empireItems.empireItems[id]?.clone()?:return toReplace
        for (enchantment in toReplace.enchantments.keys)
            item.addUnsafeEnchantment(enchantment,toReplace.enchantments[enchantment]?:continue)

        meta.persistentDataContainer.set(EmpirePlugin.empireConstants.FIXED_ITEM,
            PersistentDataType.SHORT,0)
        toReplace.itemMeta = item.itemMeta
        return item
    }



    @EventHandler
    fun PlayerInteractEvent(e: PlayerInteractEvent) {
        replaceWrongItem(e.item)
        replaceWrongItem(e.player.inventory.itemInMainHand)
        replaceWrongItem(e.player.inventory.itemInOffHand)
    }


    @EventHandler
    fun PlayerItemDamageEvent(e: PlayerItemDamageEvent) {
        replaceWrongItem(e.item)
        replaceWrongItem(e.player.inventory.itemInMainHand)
        replaceWrongItem(e.player.inventory.itemInOffHand)
    }
    @EventHandler
    fun PlayerItemHeldEvent(e: PlayerItemHeldEvent) {
        replaceWrongItem(e.player.inventory.itemInMainHand)
        replaceWrongItem(e.player.inventory.itemInOffHand)
    }


    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    fun onDisable() {
        PlayerItemHeldEvent.getHandlerList().unregister(this)
        PlayerInteractEvent.getHandlerList().unregister(this)
        PlayerItemDamageEvent.getHandlerList().unregister(this)
    }
}