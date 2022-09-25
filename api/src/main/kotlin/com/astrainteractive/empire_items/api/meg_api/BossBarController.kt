package com.astrainteractive.empire_items.api.meg_api

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.BukkitMain
import com.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.astralibs.utils.valueOfOrNull
import com.astrainteractive.empire_items.api.meg_api.EmpireModelEngineAPI
import com.astrainteractive.empire_items.api.models.mob.YmlMob
import com.astrainteractive.empire_items.api.utils.IManager
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
import java.util.UUID

private const val customMobBossBarKey = "esmp"

object BossBarController : IManager {
    private var bossBarScheduler: BukkitTask? = null
    val empireMobsBossBars: Sequence<KeyedBossBar>
        get() = Bukkit.getBossBars().asSequence().filter { it.key.key.contains(customMobBossBarKey) }

    private fun createBossBarKey(entity: Entity) =
        NamespacedKey(AstraLibs.instance, "${entity.uniqueId.toString()}_$customMobBossBarKey")

    private fun entityUUIDFromBossBar(bar: KeyedBossBar) = bar.key.key.split("_").firstOrNull()?.let(UUID::fromString)


    fun create(e: Entity, bossBar: YmlMob.YmlMobBossBar): KeyedBossBar {
        val key = createBossBarKey(e)
        println("Creating bossbar: ${key.key}")
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
        bossBarScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(AstraLibs.instance, Runnable {
            AsyncHelper.launch(Dispatchers.IO) {
                empireMobsBossBars.toList().forEach { bar ->
                    Bukkit.getOnlinePlayers().forEach { player ->
                        val uuid = kotlin.runCatching { entityUUIDFromBossBar(bar) }.getOrNull() ?: return@forEach
                        val entity = withContext(Dispatchers.BukkitMain) { Bukkit.getEntity(uuid) } ?: return@forEach

                        val entityInfo = EmpireModelEngineAPI.getEmpireEntity(entity) ?: run {
                            bar.destroy()
                            return@forEach
                        }
                        if (player.location.world != entityInfo.entity.location.world)
                            bar.removePlayer(player)
                        else if (player.location.distance(entityInfo.entity.location) > 70)
                            bar.removePlayer(player)
                        else bar.addPlayer(player)
                    }
                }
            }

        }, 0L, 20L)
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