package com.astrainteractive.empireprojekt.empire_items.events.genericevents.drop

import com.astrainteractive.empireprojekt.empire_items.api.drop.DropManager
import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.empireprojekt.empire_items.api.drop.AstraDrop
import com.astrainteractive.empireprojekt.empire_items.api.items.BlockParser
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager
import com.astrainteractive.empireprojekt.empire_items.api.items.data.ItemManager.getItemStack
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.random.Random

class ItemDropListener : IAstraListener {

    private val blockLocations: MutableList<Location> = mutableListOf()




    private fun dropItem(list: List<AstraDrop>, l: Location): Boolean {
        var isDropped = false
        for (drop in list) {
            val dropChance = Random.nextDouble(0.0, 100.0)
            if (drop.percent > dropChance) {
                isDropped = true
                val amount = Random.nextInt(drop.minAmount, drop.maxAmount + 1)
                if (amount<=0)
                    return isDropped
                val item = drop.id.getItemStack(amount)?:return isDropped
                l.world?.dropItem(l,item) ?: return isDropped
            }
        }
        return isDropped
    }


    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val block: Block = e.block
        if (blockLocations.contains(block.location))
            return
        else
            blockLocations.add(block.location)
        if (blockLocations.size > 60)
            blockLocations.removeAt(0)


        val customBlockData = BlockParser.getBlockData(e.block)
        val customBlockId =ItemManager.getBlockInfoByData(customBlockData)

        if (dropItem(DropManager.getDrops()[customBlockId?:block.blockData.material.name]?: listOf(), block.location))
            e.isDropItems = false

    }


    @EventHandler
    fun onMobDeath(e: EntityDeathEvent) {
        val entity: Entity = e.entity
        val listDrop = DropManager.getDrops()[entity.type.name] ?: return
        dropItem(listDrop, entity.location)
    }

    override fun onDisable() {
        EntityDeathEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)

    }
}