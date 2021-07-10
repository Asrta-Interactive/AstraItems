package com.makeevrserg.empireprojekt.events.genericlisteners

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.util.EmpireUtils
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Particle
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
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
        fun manageCommand(empireCommands: List<EmpireCommandEvent>) {
            for (command: EmpireCommandEvent in empireCommands)
                if (command.asConsole)
                    if (instance.server.pluginManager.getPlugin("placeholderapi") != null)
                        instance.server.dispatchCommand(
                            instance.server.consoleSender,
                            PlaceholderAPI.setPlaceholders(p, command.command)
                        )
                    else
                        instance.server.dispatchCommand(instance.server.consoleSender, command.command)
                else
                    if (instance.server.pluginManager.getPlugin("placeholderapi") != null)
                        p.performCommand(PlaceholderAPI.setPlaceholders(p, command.command))
                    else
                        p.performCommand(command.command)
        }

        fun manageSound(empireSound: List<EmpireSoundEvent>) {
            for (sound: EmpireSoundEvent in empireSound)
                p.world.playSound(p.location, sound.name, sound.volume.toFloat(), sound.pitch.toFloat())
        }

        fun manageParticle(empireParticle: List<EmpireParticleEvent>) {

            for (particle: EmpireParticleEvent in empireParticle)
                p.world.spawnParticle(
                    Particle.valueOf(particle.name),
                    p.location.x, p.location.y + 2, p.location.z,
                    particle.count, 0.0, 0.0, 0.0, particle.time
                )
        }

        fun managePotionAdd(effects: List<PotionEffect>) {
            p.addPotionEffects(effects)
        }

        fun managePotionRemove(effects: List<PotionEffectType>) {
            for (effect: PotionEffectType in effects)
                p.removePotionEffect(effect)
        }

        val humanEntity = p as HumanEntity
        if (humanEntity.hasCooldown(p.inventory.itemInMainHand.type))
            return
        for (event: EmpireEvent in EmpirePlugin.empireItems.empireEvents[id] ?: return) {
            if (actionName !in event.eventName)
                continue
            if (event.cooldown > 0)
                humanEntity.setCooldown(p.inventory.itemInMainHand.type, event.cooldown)
            manageCommand(event.commandsPlay)
            manageSound(event.soundsPlay)
            manageParticle(event.particlesPlay)
            managePotionAdd(event.potionEffectsAdd)
            managePotionRemove(event.potionEffectRemove)
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