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
        if (event.entity is Player) {
            initEventByHandler(event.entity as Player, event.eventName)
        }
    }


    private fun initEventByHandler(p: Player, eventName: String) {

        val idMainHand = EmpireUtils.getEmpireID(p.inventory.itemInMainHand)

        val idOffHand = EmpireUtils.getEmpireID(p.inventory.itemInOffHand)
        if (idMainHand != null)
            handleEvent(idMainHand, p, eventName)
        if (idOffHand != null)
            handleEvent(idOffHand, p, eventName)
    }

    private fun handleEvent(id: String, p: Player, actionName: String) {
        fun manageCommand(empireCommands: List<Command>) {
            for (command in empireCommands)
                if (command.asConsole)
                    if (instance.server.pluginManager.getPlugin("placeholderapi") != null)
                        instance.server.dispatchCommand(
                            instance.server.consoleSender,
                            PlaceholderAPI.setPlaceholders(p, command.command)
                        )
                    else {
                        instance.server.dispatchCommand(instance.server.consoleSender, command.command)
                    }
                else
                    if (instance.server.pluginManager.getPlugin("placeholderapi") != null)
                        p.performCommand(PlaceholderAPI.setPlaceholders(p, command.command))
                    else
                        p.performCommand(command.command)
        }

        fun manageSound(empireSound: Sound?) {
            empireSound ?: return
            p.world.playSound(p.location, empireSound.name, empireSound.volume.toFloat(), empireSound.pitch.toFloat())

        }

        fun manageParticle(empireParticle: ParticleBuilder?) {
            empireParticle ?: return
            empireParticle.location(p.location).spawn()
        }

        fun managePotionAdd(effects: List<PotionEffect>) {
            p.addPotionEffects(effects)
        }

        fun managePotionRemove(effects: List<PotionEffectType>) {
            for (effect: PotionEffectType in effects)
                p.removePotionEffect(effect)
        }
        fun manageDurability(item:ItemStack){
            var durability = item.itemMeta?.persistentDataContainer?.get(EmpirePlugin.empireConstants.EMPIRE_DURABILITY,
                PersistentDataType.INTEGER)?:return
            durability-=1
            item.itemMeta?.persistentDataContainer?.set(EmpirePlugin.empireConstants.EMPIRE_DURABILITY,
                PersistentDataType.INTEGER,durability)?:return
            if (durability<=0)
                item.amount-=1

        }

        val humanEntity = p as HumanEntity
        if (humanEntity.hasCooldown(p.inventory.itemInMainHand.type))
            return
        for (event in EmpirePlugin.empireItems.empireEvents[id] ?: return) {
            if (actionName !in event.eventNames)
                continue
            if (event.cooldown > 0)
                humanEntity.setCooldown(p.inventory.itemInMainHand.type, event.cooldown)
            manageCommand(event.commands)
            manageSound(event.soundsPlay)
            manageParticle(event.particlePlay)
            managePotionAdd(event.potionEffectsAdd)
            managePotionRemove(event.potionEffectsRemove)
            manageDurability(p.inventory.itemInMainHand)
            manageDurability(p.inventory.itemInOffHand)
        }
    }


    fun onDisable() {
        PlayerInteractEvent.getHandlerList().unregister(this)
        PlayerItemConsumeEvent.getHandlerList().unregister(this)
        EntityDamageEvent.getHandlerList().unregister(this)
        SignChangeEvent.getHandlerList().unregister(this)
        PlayerEditBookEvent.getHandlerList().unregister(this)
    }


}