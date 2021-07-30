package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.BlockGenerationEvent2

class MushroomBlockEventHandler {

    val blockPlace = MushroomBlockPlaceEvent()
    val mushroomCancelEvent = MushroomCancelEvent()
    val mushroomBreakEvent = MushroomBlockBreakEvent()
    val hardnessEvent = BlockHardnessEvent()
    var blockGenerationEvent: BlockGenerationEvent2? = null
    val blockTestEvent = MushroomBlockTestEvent()

    init {
        if (EmpirePlugin.config.generateBlocks)
            blockGenerationEvent = BlockGenerationEvent2()

    }

    public fun onDisable() {
        blockPlace.onDisable()
        mushroomCancelEvent.onDisable()
        mushroomBreakEvent.onDisable()
        hardnessEvent.onDisable()
        blockTestEvent.onDisable()
        if (blockGenerationEvent != null)
            blockGenerationEvent!!.onDisable()

    }
}