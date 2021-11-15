package com.astrainteractive.empireprojekt.empire_items.events.genericevents

import com.destroystokyo.paper.ParticleBuilder
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.api.ItemsAPI
import com.astrainteractive.empireprojekt.items.data.interact.CommandEvent
import com.astrainteractive.empireprojekt.items.data.interact.ParticleEvent
import com.astrainteractive.empireprojekt.items.data.interact.PotionEffectEvent
import com.astrainteractive.empireprojekt.empire_items.items.data.interact.Sound
import com.astrainteractive.empireprojekt.empire_items.util.BetterConstants
import com.astrainteractive.empireprojekt.empire_items.util.EmpireUtils
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.EntityType
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class GenericEventManager {
    companion object {
        private fun manageSound(p: Player, empireSounds: List<Sound>?) {
            empireSounds ?: return
            for (empireSound in empireSounds)
                p.world.playSound(
                    p.location,
                    empireSound.song,
                    empireSound.volume?.toFloat() ?: 1.0f,
                    empireSound.pitch?.toFloat() ?: 1.0f
                )

        }

        private fun manageParticle(p: Player, empireParticle: List<ParticleEvent>?) {
            empireParticle ?: return
            for (particle in empireParticle)
            ParticleBuilder(EmpireUtils.valueOfOrNull<Particle>(particle.name)?:continue)
                .count(particle.count)
                .extra(particle.time)
                .location(p.location.add(0.0,1.5,0.0)).spawn()
        }

        private fun managePotionAdd(p: Player, effects: List<PotionEffectEvent>?) {
            effects ?: return
            Bukkit.getScheduler().callSyncMethod(EmpirePlugin.instance) {
                for (effect in effects)
                    p.addPotionEffect(PotionEffect(PotionEffectType.getByName(effect.effect)?:continue,effect.duration,effect.amplifier))
            }
        }

        private fun managePotionRemove(p: Player, effects: List<String>?) {
            effects ?: return
            Bukkit.getScheduler().callSyncMethod(EmpirePlugin.instance) {
                for (effect in effects)
                    p.removePotionEffect(PotionEffectType.getByName(effect)?:continue)
            }
        }

        private fun manageDurability(item: ItemStack) {
            var durability = item.itemMeta?.persistentDataContainer?.get(
                BetterConstants.EMPIRE_DURABILITY.value,
                PersistentDataType.INTEGER
            ) ?: return
            durability -= 1
            item.itemMeta?.persistentDataContainer?.set(
                BetterConstants.EMPIRE_DURABILITY.value,
                PersistentDataType.INTEGER, durability
            ) ?: return
            if (durability <= 0)
                item.amount -= 1

        }

        private fun manageEntitySpawn(p: Player, map: Map<String, Int>?) {
            map ?: return
            for ((entity, amount) in map)
                p.location.world?.spawnEntity(p.location, EntityType.fromName(entity) ?: return) ?: continue

        }

        private fun executeServerCommand(cmd: String) {
            EmpirePlugin.instance.server.dispatchCommand(EmpirePlugin.instance.server.consoleSender, cmd)
        }

        public fun manageCommand(p: Player, empireCommands: List<CommandEvent>?) {
            empireCommands ?: return
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


        fun handleEvent(id: String?, p: Player, eventName: String) {
            id ?: return
            val humanEntity = p as HumanEntity
            if (humanEntity.hasCooldown(p.inventory.itemInMainHand.type))
                return
            val events = ItemsAPI.getEventByItemId(id) ?: return
            for (event in events) {
                event.eventList ?: return
                if (eventName !in event.eventList)
                    continue

                if (event.cooldown != null && event.cooldown > 0)
                    humanEntity.setCooldown(p.inventory.itemInMainHand.type, event.cooldown)
                manageEntitySpawn(p, event.spawnEntity)
                manageCommand(p, event.playCommand)
                manageSound(p, event.playSound)
                manageParticle(p, event.playParticle)
                managePotionAdd(p, event.potionEffect)
                managePotionRemove(p, event.potionEffectsRemove)
                if (!eventName.equals("PlayerMoveEvent", ignoreCase = true)) {
                    manageDurability(p.inventory.itemInMainHand)
                    manageDurability(p.inventory.itemInOffHand)
                }
            }
        }
    }
}