package com.astrainteractive.empireprojekt.empire_items.events.blocks

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.IAstraManager
import com.astrainteractive.empireprojekt.EmpirePlugin
import com.astrainteractive.empireprojekt.empire_items.events.blocks.events.*

class MushroomBlockEventHandler() : IAstraManager {

    override val handlers: MutableList<IAstraListener> = mutableListOf()

    init {
        if (EmpirePlugin.empireConfig.generateBlocks)
            BlockGenerationEvent().onEnable(this)
        MushroomBlockPlaceEvent().onEnable(this)
        MushroomCancelEvent().onEnable(this)
        MushroomBlockBreakEvent().onEnable(this)
        BlockHardnessEvent().onEnable(this)
        MushroomBlockTestEvent().onEnable(this)

    }


}