package com.makeevrserg.empireprojekt.empire_items.events.mobs

import com.makeevrserg.empireprojekt.empire_items.events.mobs.data.EmpireMob
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import kotlin.random.Random

class EmpireMobsEvent : IEmpireListener {


    @EventHandler
    fun onMobSpawnEvent(e: EntitySpawnEvent) {


        val empireMob = MobAPI().getEmpireMobByEntity(e.entity) ?: return
        val location = e.location.clone()
        if (EmpireMobsManager.spawnList.contains(location)) {
            EmpireMobsManager.spawnList.remove(location)
            return
        }
        e.isCancelled = true

        MobAPI().spawnMob(location, empireMob)


    }


    @EventHandler
    fun entityAnimationEvent(e: EntityMoveEvent) {
        val mobAPI = MobAPI()
        val entity = mobAPI.getLivingEntity(e.entity) ?: return
        val empireMob = mobAPI.getEmpireMob(e.entity) ?: return

        if (e.to.pitch < -15)
            e.to.pitch = -15.0f

        if (0.2>Random.nextDouble(100.0))
            entity.location.world?.playSound(entity.location,empireMob.soundIdle?:"",1.0f,1.0f)


        if (System.currentTimeMillis().minus(attackCooldown[entity] ?: 0) < 1000)
            return
        else
            attackCooldown.remove(entity)

        if (e.from.distance(e.to) < 0.08)
            mobAPI.changeMobState(entity, empireMob, EmpireMob.STATE.IDLE)
        else
            mobAPI.changeMobState(entity, empireMob, EmpireMob.STATE.WALK)

    }

    val attackCooldown = mutableMapOf<LivingEntity, Long>()

    @EventHandler
    fun  entityDieEvent(e:EntityDeathEvent){

        val mobAPI = MobAPI()
        val entity = e.entity
        val empireMobAttacker = mobAPI.getEmpireMob(e.entity)?:return
        entity.location.world?.playSound(entity.location,empireMobAttacker.soundDie?:"",1.0f,1.0f)

    }

    @EventHandler
    fun entityAttackEvent(e: EntityDamageByEntityEvent) {
        val mobAPI = MobAPI()
        val entity = mobAPI.getLivingEntity(e.damager) ?: return
        val empireMobAttacker = mobAPI.getEmpireMob(e.entity)
        if (empireMobAttacker!=null)
            entity.location.world?.playSound(entity.location,empireMobAttacker.soundHurt?:"",1.0f,1.0f)

        val empireMobDamager = mobAPI.getEmpireMob(e.damager) ?: return

        attackCooldown[entity] = System.currentTimeMillis()
        mobAPI.changeMobState(entity, empireMobDamager, EmpireMob.STATE.ATTACK)
    }



    public override fun onDisable() {
        EntitySpawnEvent.getHandlerList().unregister(this)
        EntityMoveEvent.getHandlerList().unregister(this)
        EntityDamageByEntityEvent.getHandlerList().unregister(this)
        EntityDeathEvent.getHandlerList().unregister(this)
    }
}