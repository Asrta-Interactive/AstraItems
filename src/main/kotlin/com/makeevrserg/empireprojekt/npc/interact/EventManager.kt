package com.makeevrserg.empireprojekt.npc.interact

import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.IEventManager

class EventManager :IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()
    init {
        ProtocolLibPacketListener().onEnable(this)
        ClickNPC().onEnable(this)
    }
}