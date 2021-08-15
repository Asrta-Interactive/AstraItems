package com.makeevrserg.empireprojekt.events.mobs

import empirelibs.IEmpireListener
import empirelibs.IEventManager

class EventManager :IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()
    init {
        if (EmpireMobsManager.empireMobs.isNotEmpty())
             EmpireMobsEvent()
    }
}