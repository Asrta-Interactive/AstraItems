package com.astrainteractive.empire_items.meg

import com.astrainteractive.empire_items.meg.api.EmpireModelEngineAPI
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.utils.convertHex
import ru.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_itemss.api.utils.IManager
import com.astrainteractive.empire_itemss.api.utils.destroy
import com.atrainteractive.empire_items.models.mob.YmlMob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.KeyedBossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitTask
import ru.astrainteractive.astralibs.di.IReloadable
import ru.astrainteractive.astralibs.di.getValue
import java.util.UUID


class BossBarController() : IManager {
    private val customMobBossBarKey = "esmp"
    val empireMobsBossBars: Sequence<KeyedBossBar>
        get() = Bukkit.getBossBars().asSequence().filter { it.key.key.contains(customMobBossBarKey) }

    private fun createBossBarKey(entity: Entity) =
        NamespacedKey(AstraLibs.instance, "${entity.uniqueId.toString()}_$customMobBossBarKey")

    fun entityUUIDFromBossBar(bar: KeyedBossBar) = bar.key.key.split("_").firstOrNull()?.let(UUID::fromString)


    fun create(e: Entity, bossBar: YmlMob.YmlMobBossBar): KeyedBossBar {
        val key = createBossBarKey(e)
        val barColor = valueOfOrNull<BarColor>(bossBar.color) ?: BarColor.RED
        val barStyle = valueOfOrNull<BarStyle>(bossBar.barStyle) ?: BarStyle.SOLID
        val barFlags = bossBar.flags.mapNotNull { valueOfOrNull<BarFlag>(it) }.toTypedArray()
        val bar =
            Bukkit.createBossBar(key, convertHex(bossBar.name), barColor, barStyle, *barFlags)
        bar.isVisible = true
        bar.progress = 1.0
        return bar
    }

    fun onEntityDamaged(e: Entity) {
        val bar = Bukkit.getBossBar(createBossBarKey(e)) ?: return
        val livngEntity = (e as? LivingEntity) ?: return
        val maxHealth = livngEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: livngEntity.maxHealth
        bar.progress = (livngEntity.health / maxHealth).coerceIn(0.0,1.0)
    }

    fun onEntityDead(e: Entity) {
        Bukkit.getBossBar(createBossBarKey(e))?.destroy()
        Bukkit.removeBossBar(createBossBarKey(e))
    }

    override fun onEnable() {
    }


    override fun onDisable() {
        empireMobsBossBars.forEach {
            it.isVisible = false
            it.destroy()

        }
    }
}

