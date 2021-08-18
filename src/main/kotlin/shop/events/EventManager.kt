package shop.events

import empirelibs.IEmpireListener
import empirelibs.IEventManager

class EventManager() :IEventManager {
    override val handlers: MutableList<IEmpireListener> = mutableListOf()
    init {

    }
}