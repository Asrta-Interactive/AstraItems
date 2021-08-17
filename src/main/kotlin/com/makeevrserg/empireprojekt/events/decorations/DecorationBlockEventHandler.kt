package com.makeevrserg.empireprojekt.events.decorations

import makeevrserg.empireprojekt.events.decorations.events.DecorationBlockPlaceEvent
import empirelibs.IEmpireListener
import empirelibs.IEventManager

class DecorationBlockEventHandler :IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()
    init {
        DecorationBlockPlaceEvent().onEnable(this)
    }
}