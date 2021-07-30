package com.makeevrserg.empireprojekt.events.mobs

import com.makeevrserg.empireprojekt.EmpirePlugin
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.random.Random

class EmpireMobs : Listener {


    private fun getLivingEntity(e: Entity): LivingEntity? {
        if (e !is LivingEntity)
            return null
        return e as LivingEntity
    }

    private fun setNameTag(e: Entity, tag: String) {
        (e as CraftEntity).handle.addScoreboardTag(tag)
    }

    private fun getNameTag(e: Entity): MutableSet<String>? {
        return (e as CraftEntity).handle.scoreboardTags
    }

    private fun getEmpireMob(e: Entity): EmpireMobsManager.EmpireMob? {
        for (tag in (e as CraftEntity).handle.scoreboardTags) {
            val mob = EmpireMobsManager.empireMobs[tag]
            if (mob != null)
                return mob
        }
        return null
    }


    private fun getMobToSpawn(list: List<EmpireMobsManager.EmpireMob>, entity: Entity): MutableList<EmpireMobsManager.EmpireMob> {
        val mobs = mutableListOf<EmpireMobsManager.EmpireMob>()
        for (mob in list)
            if (mob.replaceMobSpawn[entity.type]?.chance ?: continue > Random.nextDouble(100.0))
                mobs.add(mob)
        return mobs
    }


    @EventHandler
    fun onMobSpawnEvent(e: EntitySpawnEvent) {

        val entity = getLivingEntity(e.entity) ?: return
        val empireMobs = EmpireMobsManager.empireMobsByEntitySpawn[entity.type] ?: return
        val mobsToSpawn = getMobToSpawn(empireMobs,entity)
        if (mobsToSpawn.isEmpty())
            return
        val empireMob = mobsToSpawn[Random.nextInt(mobsToSpawn.size)]


        for (attr in empireMob.attributes)
            entity.getAttribute(attr.attribute)?.baseValue = Random.nextDouble(attr.min, attr.max + 0.0001)



        setNameTag(e.entity, empireMob.id)
        entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 99999999, false, false, false))
        entity.equipment!!.helmet = empireMob.idleAnimation
        entity.isSilent = true
    }

    @EventHandler
    fun entityAnimationEvent(e: EntityMoveEvent) {
        val entity = getLivingEntity(e.entity) ?: return
        val empireMob = getEmpireMob(e.entity) ?: return

        if (System.currentTimeMillis().minus(attackCooldown[entity] ?: 0) < 1000)
            return
        else
            attackCooldown.remove(entity)

        if (e.from.distance(e.to) < 0.08)
            entity.equipment!!.helmet = empireMob.idleAnimation
        else
            entity.equipment!!.helmet = empireMob.walkAnimation
    }

    val attackCooldown = mutableMapOf<LivingEntity, Long>()

    @EventHandler
    fun entityAttackEvent(e: EntityDamageByEntityEvent) {
        val entity = getLivingEntity(e.damager) ?: return
        val empireMob = getEmpireMob(e.damager) ?: return

        attackCooldown[entity] = System.currentTimeMillis()
        entity.equipment!!.helmet = empireMob.attackAnimation
    }

    init {
        EmpirePlugin.instance.server.pluginManager.registerEvents(this, EmpirePlugin.instance)
    }

    public fun onDisable() {
        EntitySpawnEvent.getHandlerList().unregister(this)
        EntityMoveEvent.getHandlerList().unregister(this)
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
    }
}