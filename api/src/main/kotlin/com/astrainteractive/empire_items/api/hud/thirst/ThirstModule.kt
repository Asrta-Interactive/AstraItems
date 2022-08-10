package com.astrainteractive.empire_items.api.hud.thirst

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.empire_items.api.hud.HudView
import org.bukkit.Bukkit

class ThirstModule : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()

    companion object {
        lateinit var file: FileManager
    }

    fun onEnable() {
        file = FileManager("modules/thirst.yml")
        Events().onEnable(this)
        Bukkit.getOnlinePlayers().forEach {
            ThirstService.addPlayer(it)
            HudView.onPlayerJoin(it)
        }
        HudView.onEnable()
    }

    override fun onDisable() {
        HudView.onDisable()
        super.onDisable()

    }
}