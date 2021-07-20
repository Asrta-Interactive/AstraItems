package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.BlockGenerationEvent

class MushroomBlockEventHandler {

    val blockPlace = MushroomBlockPlaceEvent()
    val mushroomCancelEvent = MushroomCancelEvent()
    val mushroomBreakEvent = MushroomBlockBreakEvent()
    val hardnessEvent = BlockHardnessEvent()
    var blockGenerationEvent: BlockGenerationEvent? = null

    init {
        if (EmpirePlugin.config.generateBlocks)
            blockGenerationEvent = BlockGenerationEvent()

    }

    public fun onDisable() {
        blockPlace.onDisable()
        mushroomCancelEvent.onDisable()
        mushroomBreakEvent.onDisable()
        hardnessEvent.onDisable()
        if (blockGenerationEvent != null)
            blockGenerationEvent!!.onDisable()

    }
}