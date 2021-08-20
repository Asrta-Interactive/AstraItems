package com.makeevrserg.empireprojekt.empire_items.events.mobs

import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.IEventManager

class EventManager :IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()
    init {
        if (EmpireMobsManager.empireMobs.isNotEmpty())
             EmpireMobsEvent()
    }
}