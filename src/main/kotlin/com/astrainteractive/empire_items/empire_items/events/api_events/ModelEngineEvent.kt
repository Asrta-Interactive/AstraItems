package com.astrainteractive.empire_items.empire_items.events.api_events

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.mobs.BossBarManager
import com.astrainteractive.empire_items.api.mobs.MobApi
import com.astrainteractive.empire_items.api.models.yml_item.Interact
import com.astrainteractive.empire_items.modules.boss_fight.PlayersInviteViewModel
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.PlayerDeathEvent
import kotlin.random.Random

class ModelEngineEvent : EventListener {


    val bossBarScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(EmpirePlugin.instance, Runnable {

        BossBarManager.bossBars.toMap().forEach bossBarsEntities@{
            val entity = it.key
            val bossBar = it.value

            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.location.distance(entity.location) > 70)
                    bossBar.removePlayer(player)
                else bossBar.addPlayer(player)

            }
        }
    }, 0L, 5L)
    val entitySoundScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(EmpirePlugin.instance, Runnable {
        MobApi.activeMobs.forEach { mobInfo ->
            if (Random.nextDouble(100.0) < 5) {
                if (mobInfo.ymlMob.idleSound.isNotEmpty())
                    Interact.PlaySound(mobInfo.ymlMob.idleSound[Random.nextInt(mobInfo.ymlMob.idleSound.size)])
                        .play(mobInfo.entity.location)
            }
            MobApi.executeAction(mobInfo, "onTick")
        }
    }, 0L, 20L)


    val onMobSpawn = DSLEvent.event(EntitySpawnEvent::class.java) { e ->
        if (MobApi.isSpawnIgnored(e.location))
            return@event
        val mobs = MobApi.getByNaturalSpawn(e.entity) ?: return@event
        MobApi.replaceEntity(mobs.shuffled().first(), e.entity, naturalSpawn = true)
    }


    val entityMove = DSLEvent.event(EntityMoveEvent::class.java) { e ->
        val empireMob = MobApi.getCustomEntityInfo(e.entity) ?: return@event
        val event = empireMob.ymlMob.events["onMove"] ?: return@event
        MobApi.executeEvent(empireMob.entity, empireMob.activeModel, event, "onMove")
    }

    val onEntityTarget = DSLEvent.event(EntityTargetEvent::class.java) { e ->
        val name = e.target?.type?.name?.uppercase() ?: return@event
        val entityInfo = MobApi.getCustomEntityInfo(e.entity) ?: return@event

        if (entityInfo.ymlMob.ignoreMobs.filter { it.equals(name, ignoreCase = true) }.isNotEmpty())
            e.isCancelled = true
    }

    val onMobDamage = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        val entityInfo = MobApi.getCustomEntityInfo(e.entity) ?: return@event

        val livingEntity = (e.entity as LivingEntity)
        BossBarManager.bossBars[e.entity]?.let {
            it.progress = livingEntity.health / livingEntity.maxHealth
        }
        val event = entityInfo.ymlMob.events["onDamaged"]?.let {
            MobApi.executeEvent(e.entity, entityInfo.activeModel, it, "onDamaged")
            it.playPotionEffect.forEach { (key, effect) ->
                effect.play(e.damager as? LivingEntity)
            }
            Attribute.GENERIC_MAX_HEALTH
        }
        MobApi.executeAction(entityInfo, "onDamaged")
        if (livingEntity.health - e.damage < 0)
            entityInfo.ymlMob.events["onDeath"]?.let {
                MobApi.executeEvent(e.entity, entityInfo.activeModel, it, "onDeath")
            }


    }

    val onDamage = DSLEvent.event(EntityDamageByEntityEvent::class.java) { e ->
        if (e.entity !is LivingEntity)
            return@event
        val entityInfo = MobApi.getCustomEntityInfo(e.damager) ?: return@event
        if (MobApi.isAttackAnimationTracked(e.damager) || entityInfo.ymlMob.hitDelay < 0) {
            MobApi.stopAttackAnimationTrack(e.damager)
            entityInfo.ymlMob.events["onDamage"]?.let {
                it.actions
            }
            return@event
        }
        e.isCancelled = true
        MobApi.executeAction(entityInfo, "onDamage")
        MobApi.performAttack(entityInfo, listOf(e.entity), e.damage)
    }

    val onDeath = DSLEvent.event(EntityDeathEvent::class.java) { e ->
        removeEntity(e.entity)
    }
    val onRemovedFromWorld = DSLEvent.event(EntityRemoveFromWorldEvent::class.java) { e ->
        removeEntity(e.entity)
    }
    fun removeEntity(entity:org.bukkit.entity.Entity){
        BossBarManager.deleteEntityBossBar(entity)
        val entityInfo = MobApi.getCustomEntityInfo(entity) ?: return
        val event = entityInfo.ymlMob.events["onDeath"] ?: return
        MobApi.executeEvent(entity, entityInfo.activeModel, event, "onDeath")
    }

    private val onBossKillEvent = DSLEvent.event(EntityDeathEvent::class.java) {
        if (it.entity.entityId == PlayersInviteViewModel.customEntityInfo?.entity?.entityId) {
            PlayersInviteViewModel.customEntityInfo = null
            PlayersInviteViewModel.executor = null
            PlayersInviteViewModel.currentTeam.clear()
        }
    }
    private val bossKilledPlayerEvent = DSLEvent.event(PlayerDeathEvent::class.java) {
        if (PlayersInviteViewModel.executor == null || PlayersInviteViewModel.customEntityInfo == null) return@event
        PlayersInviteViewModel.currentTeam.remove(it.player)
        if (PlayersInviteViewModel.currentTeam.isEmpty()) {
            val entity = PlayersInviteViewModel.customEntityInfo
            val l = entity?.entity?.location
            entity?.entity?.let(BossBarManager::deleteEntityBossBar)
            entity?.entity?.remove()
            entity?.modeledEntity?.removeModel(entity.activeModel.modelId)

            PlayersInviteViewModel.customEntityInfo = null
            PlayersInviteViewModel.executor = null
            PlayersInviteViewModel.currentTeam.clear()
            l?.getNearbyEntities(200.0, 200.0, 200.0)?.forEach {
                if (it !is Player) it.remove()
            }
        }
    }


    override fun onDisable() {
        bossBarScheduler.cancel()
        entitySoundScheduler.cancel()
    }
}