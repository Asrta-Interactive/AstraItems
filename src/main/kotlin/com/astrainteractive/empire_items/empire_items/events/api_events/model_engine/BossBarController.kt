package com.astrainteractive.empire_items.empire_items.events.api_events.model_engine

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import com.astrainteractive.empire_items.api.utils.IManager
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

private const val customMobBossBarKey = "esmp"

class BossBarController : IManager {
    private var bossBarScheduler: BukkitTask? = null
    val empireMobsBossBars: Sequence<KeyedBossBar>
        get() = Bukkit.getBossBars().asSequence().filter { it.key.key.contains(customMobBossBarKey) }

    fun createBossBarKey(entity: Entity) = NamespacedKey(AstraLibs.instance, "${entity.entityId}_$customMobBossBarKey")
    fun entityIdFromBossBar(bar: KeyedBossBar) = bar.key.key.split("_").firstOrNull()?.toIntOrNull()


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
        bar.progress = (livngEntity.health / maxHealth)
    }

    fun onEntityDead(e: Entity) {
        Bukkit.getBossBar(createBossBarKey(e))?.destroy()
        Bukkit.removeBossBar(createBossBarKey(e))
    }

    override suspend fun onEnable() {
        bossBarScheduler = bukkitAsyncTimer {
            Bukkit.getOnlinePlayers().forEach { player ->
                empireMobsBossBars.forEach { bar ->
                    val id = entityIdFromBossBar(bar) ?: return@forEach
                    val entityInfo = ModelEngineApi.mobByEntityID(id) ?: run {
                        bar.destroy()
                        return@forEach
                    }

                    if (player.location.distance(entityInfo.entity.location) > 70)
                        bar.removePlayer(player)
                    else bar.addPlayer(player)
                }
            }
        }
    }

    override suspend fun onDisable() {
        bossBarScheduler?.cancel()
        empireMobsBossBars.forEach {
            it.isVisible = false
            it.destroy()

        }
    }
}

fun KeyedBossBar.destroy() {
    Bukkit.getOnlinePlayers().forEach { removePlayer(it) }
    removeAll()
}