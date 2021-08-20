package com.makeevrserg.empireprojekt.empire_items.events.mobs

import com.makeevrserg.empireprojekt.empire_items.events.mobs.data.EmpireMob
import org.bukkit.Location

class EmpireMobsManager {
    companion object {
        lateinit var instance: EmpireMobsManager
            private set
        val empireMobs: List<EmpireMob> = EmpireMob.new()
        lateinit var mobById: Map<String, EmpireMob>
            private set
        lateinit var empireMobsByEntitySpawn: Map<String, List<EmpireMob>>
            private set
        val spawnList = mutableListOf<Location>()
    }

    private val eventManager = EventManager()

    init {
        instance = this
        initMobById()
        initMobsByEntity()
    }

    private fun initMobsByEntity() {
        val map = mutableMapOf<String, MutableList<EmpireMob>>()
        for (mob in empireMobs)
            for (replaceMob in mob.mobSpawnReplace) {
                if (!map.containsKey(replaceMob.type))
                    map[replaceMob.type] = mutableListOf(mob)
                else
                    map[replaceMob.type]!!.add(mob)
            }
        empireMobsByEntitySpawn = map
    }

    private fun initMobById() {
        val mobMutMap = mutableMapOf<String, EmpireMob>()
        for (mob in empireMobs) {
            mobMutMap[mob.id] = mob
        }
        mobById = mobMutMap
    }

    public fun onDisable() {
        eventManager.onDisable()
    }
}