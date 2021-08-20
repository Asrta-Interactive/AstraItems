package com.makeevrserg.empireprojekt.empire_items.events.decorations

import makeevrserg.empireprojekt.events.decorations.events.DecorationBlockPlaceEvent
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.IEventManager

class DecorationBlockEventHandler :IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()
    init {
        DecorationBlockPlaceEvent().onEnable(this)
    }
}