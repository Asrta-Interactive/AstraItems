package com.makeevrserg.empireprojekt.events.genericevents

import com.destroystokyo.paper.ParticleBuilder
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.items.Command
import com.makeevrserg.empireprojekt.items.Sound
import com.makeevrserg.empireprojekt.util.EmpireUtils
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ItemInteractListener : Listener {

    init {
        instance.server.pluginManager.registerEvents(this, instance)
    }


    @EventHandler
    fun onBookEvent(e: PlayerEditBookEvent) {
        val newMeta = e.newBookMeta
        if (newMeta.hasAuthor())
            newMeta.author =
                EmpireUtils.HEXPattern(EmpireUtils.emojiPattern(newMeta.author!!))

        if (newMeta.hasTitle())
            newMeta.title =
                EmpireUtils.HEXPattern(EmpireUtils.emojiPattern(newMeta.title!!))
        for (i in 1..newMeta.pageCount) {
            newMeta.setPage(
                i, EmpireUtils.HEXPattern(
                    EmpireUtils.emojiPattern(
                        newMeta.getPage(i)
                    ) + "&r"
                )
            )
        }
        e.newBookMeta = newMeta
    }

    @EventHandler
    fun onSignEvent(e: SignChangeEvent) {

        for (i in e.lines.indices)
            e.setLine(
                i,
                EmpireUtils.HEXPattern(EmpireUtils.emojiPattern(e.getLine(i) ?: continue))
            )


    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        initEventByHandler(event.player, event.action.name)
    }

    @EventHandler
    fun onDrink(event: PlayerItemConsumeEvent) {
        initEventByHandler(event.player, event.eventName)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player)
            initEventByHandler(event.entity as Player, event.eventName)

    }


    private fun initEventByHandler(p: Player, eventName: String) {

        val idMainHand = EmpireUtils.getEmpireID(p.inventory.itemInMainHand)

        val idOffHand = EmpireUtils.getEmpireID(p.inventory.itemInOffHand)
            GenericEventHandler.handleEvent(idMainHand, p, eventName)
            GenericEventHandler.handleEvent(idOffHand, p, eventName)
    }



    fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
        PlayerItemConsumeEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)
        SignChangeEvent.getHandlerList().unregister(this)
        PlayerEditBookEvent.getHandlerList().unregister(this)
    }


}