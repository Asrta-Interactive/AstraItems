package com.makeevrserg.empireprojekt.events.mobs

import com.makeevrserg.empireprojekt.EmpirePlugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import java.awt.geom.Point2D

class EmpireMobs : Listener {


    lateinit var spawn:Point2D

    private fun initMobs() {
        val mobFileConfig = EmpirePlugin.empireFiles.mobsFile.getConfig()
        spawn.setLocation(mobFileConfig?.getDouble("settings.spawn.x")?:0.0,mobFileConfig?.getDouble("settings.spawn.y")?:0.0)

    }

    class EmpireMob(section: ConfigurationSection){

        fun initMob(){

        }
        init {

        }



    }


    @EventHandler
    fun onMobSpawnEvent(e:EntitySpawnEvent){
        if (e.entityType!=EntityType.ZOMBIE)
            return
        val entity = e.entity
        entity.isCustomNameVisible = true
        entity.customName = "New Zombie"
    }

    init {
        //plugin.server.pluginManager.registerEvents(this, plugin)
    }

    public fun onDisable() {
        //EntitySpawnEvent.getHandlerList().unregister(this)
    }
}