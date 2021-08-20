package com.makeevrserg.empireprojekt.empire_items.events.blocks

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.events.blocks.events.*
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import com.makeevrserg.empireprojekt.empirelibs.IEventManager

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