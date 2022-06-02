package com.astrainteractive.empire_items.api.items.data

import com.astrainteractive.empire_items.api.items.data.EmpireItem.Companion.getIntOrNull
import com.astrainteractive.empire_items.api.items.data.interact.PlayCommand
import com.astrainteractive.empire_items.api.items.data.interact.PlayPotionEffect
import com.astrainteractive.empire_items.api.mobs.data.getMap
import com.astrainteractive.empire_items.api.utils.getDoubleOrNull
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

data class Gun(
    val cooldown: Int?,
    val recoil: Double?,
    val clipSize: Int?,
    val bulletWeight: Double?,
    val bulletTrace: Int,
    val particle: String?,
    val color: String?,
    val damage: Double?,
    val reload: String?,
    val reloadSound: String?,
    val fullSound: String?,
    val shootSound: String?,
    val noAmmoSound: String?,
    val radius: Double,
    val radiusSneak: Double?,
    val explosion: Int?,
    val onContact: OnContact?,
    val advanced: Advanced?
) {
    data class Advanced(
        val armorPenetration: Map<String, Double>
    ) {
        companion object {
            fun get(s: ConfigurationSection?): Advanced? {
                s ?: return null
                val armorPenetration =
                    s.getConfigurationSection("armorPenetration").getMap<String, Double>() ?: return null
                return Advanced(armorPenetration)
            }
        }
    }

    data class OnContact(
        val ignorePlayer: Boolean = false,
        val fireTicks: Int? = 0,
        val freezeTicks: Int? = 0,
        val playPotionEffect: List<PlayPotionEffect>?,
    ) {
        fun play(ent: Entity, creator: Player) {
            if (ent == creator && ignorePlayer)
                return
            ent.freezeTicks = freezeTicks ?: ent.freezeTicks
            ent.fireTicks = fireTicks ?: ent.freezeTicks
            if (ent is LivingEntity) {
                playPotionEffect?.forEach { playPotionEffect -> playPotionEffect.play(ent as LivingEntity) }

            }


        }

        companion object {
            fun get(s: ConfigurationSection?): OnContact? {
                s ?: return null
                return OnContact(
                    ignorePlayer = s.getBoolean("ignorePlayer"),
                    fireTicks = s.getIntOrNull("fireTicks"),
                    freezeTicks = s.getIntOrNull("freezeTicks"),
                    playPotionEffect = PlayPotionEffect.getMultiPlayPotionEffect(s.getConfigurationSection("playPotionEffect"))
                )
            }
        }
    }

    companion object {
        fun getGun(s: ConfigurationSection?): Gun? {
            s ?: return null
            return Gun(
                cooldown = s.getIntOrNull("cooldown"),
                recoil = s.getDoubleOrNull("recoil"),
                clipSize = s.getIntOrNull("clipSize"),
                bulletWeight = s.getDoubleOrNull("bulletWeight"),
                bulletTrace = s.getInt("bulletTrace", 100),
                color = s.getString("color"),
                damage = s.getDoubleOrNull("damage"),
                reload = s.getString("reload"),
                reloadSound = s.getString("reloadSound"),
                fullSound = s.getString("fullSound"),
                shootSound = s.getString("shootSound"),
                particle = s.getString("particle"),
                noAmmoSound = s.getString("noAmmoSound"),
                radius = s.getDouble("radius", 1.0),
                radiusSneak = s.getDoubleOrNull("radiusSneak"),
                explosion = s.getIntOrNull("explosion"),
                onContact = OnContact.get(s.getConfigurationSection("onContact")),
                advanced = Advanced.get(s.getConfigurationSection("advanced"))
            )
        }
    }
}
