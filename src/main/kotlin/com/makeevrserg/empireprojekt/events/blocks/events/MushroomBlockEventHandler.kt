package com.makeevrserg.empireprojekt.events.blocks.events

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.events.blocks.BlockGeneration

class MushroomBlockEventHandler {

    val blockPlace = MushroomBlockPlace()
    val mushroomCancelEvent = MushroomCancelEvent()
    val mushroomBreakEvent = MushroomBlockBreak()
    val hardnessEvent = BlockHardnessEvent()
    var blockGeneration: BlockGeneration? = null

    init {
        if (EmpirePlugin.config.generateBlocks)
            blockGeneration = BlockGeneration()

    }

    public fun onDisable() {
        blockPlace.onDisable()
        mushroomCancelEvent.onDisable()
        mushroomBreakEvent.onDisable()
        hardnessEvent.onDisable()
        if (blockGeneration != null)
            blockGeneration!!.onDisable()

    }
}