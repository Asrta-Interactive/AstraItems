package com.makeevrserg.empireprojekt.events.genericevents

import com.destroystokyo.paper.ParticleBuilder
import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.items.Command
import com.makeevrserg.empireprojekt.items.Sound
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class GenericEventHandler {
    companion object {
        private fun manageSound(p: Player, empireSound: Sound?) {
            empireSound ?: return
            p.world.playSound(p.location, empireSound.name, empireSound.volume.toFloat(), empireSound.pitch.toFloat())

        }

        private fun manageParticle(p: Player, empireParticle: ParticleBuilder?) {
            empireParticle ?: return
            empireParticle.location(p.location).spawn()
        }

        private fun managePotionAdd(p: Player, effects: List<PotionEffect>) {
            p.addPotionEffects(effects)
        }

        private fun managePotionRemove(p: Player, effects: List<PotionEffectType>) {
            for (effect: PotionEffectType in effects)
                p.removePotionEffect(effect)
        }

        private fun manageDurability(item: ItemStack) {
            var durability = item.itemMeta?.persistentDataContainer?.get(
                EmpirePlugin.empireConstants.EMPIRE_DURABILITY,
                PersistentDataType.INTEGER
            ) ?: return
            durability -= 1
            item.itemMeta?.persistentDataContainer?.set(
                EmpirePlugin.empireConstants.EMPIRE_DURABILITY,
                PersistentDataType.INTEGER, durability
            ) ?: return
            if (durability <= 0)
                item.amount -= 1

        }

        private fun executeServerCommand(cmd: String) {
            EmpirePlugin.instance.server.dispatchCommand(EmpirePlugin.instance.server.consoleSender, cmd)
        }

        public fun manageCommand(p: Player, empireCommands: List<Command>) {
            for (command in empireCommands) {
                var cmd = command.command
                if (EmpirePlugin.instance.server.pluginManager.getPlugin("placeholderapi") != null)
                    cmd = PlaceholderAPI.setPlaceholders(p, cmd)
                if (command.asConsole)
                    executeServerCommand(cmd)
                else
                    p.performCommand(cmd)
            }
        }

        fun handleEvent(id: String?, p: Player, actionName: String) {
            id ?: return
            val humanEntity = p as HumanEntity
            if (humanEntity.hasCooldown(p.inventory.itemInMainHand.type))
                return
            for (event in EmpirePlugin.empireItems.empireEvents[id] ?: return) {
                if (actionName !in event.eventNames)
                    continue
                if (event.cooldown > 0)
                    humanEntity.setCooldown(p.inventory.itemInMainHand.type, event.cooldown)
                manageCommand(p, event.commands)
                manageSound(p, event.soundsPlay)
                manageParticle(p, event.particlePlay)
                managePotionAdd(p, event.potionEffectsAdd)
                managePotionRemove(p, event.potionEffectsRemove)
                manageDurability(p.inventory.itemInMainHand)
                manageDurability(p.inventory.itemInOffHand)
            }
        }
    }
}