package com.makeevrserg.empireprojekt.events.blocks

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.events.*
import empirelibs.IEmpireListener
import empirelibs.IEventManager

class MushroomBlockEventHandler() :IEventManager {

    override val handlers: MutableList<IEmpireListener> = mutableListOf()
    init {
        if (EmpirePlugin.empireConfig.generateBlocks)
            BlockGenerationEvent2().onEnable(this)
        MushroomBlockPlaceEvent().onEnable(this)
        MushroomCancelEvent().onEnable(this)
        MushroomBlockBreakEvent().onEnable(this)
        BlockHardnessEvent().onEnable(this)
        MushroomBlockTestEvent().onEnable(this)

    }


}