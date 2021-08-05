package com.makeevrserg.empireprojekt.events.genericevents.drop

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.instance
import com.makeevrserg.empireprojekt.events.blocks.MushroomBlockApi
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class ItemDropListener : Listener {

    private val blockLocations: MutableList<Location> = mutableListOf()



    init {
        instance.server.pluginManager.registerEvents(this, instance)
    }

    private fun dropItem(list: List<ItemDropManager.ItemDrop>, l: Location): Boolean {
        var isDropped = false
        for (drop: ItemDropManager.ItemDrop in list) {
            val dropChance = Random.nextDouble(0.0, 100.0)
            if (drop.chance > dropChance) {
                isDropped = true
                for (i in 0 until Random.nextInt(drop.minAmount, drop.maxAmount + 1))
                    l.world?.dropItem(
                        l,
                        EmpirePlugin.empireItems.empireItems[drop.item] ?: ItemStack(
                            Material.getMaterial(drop.item) ?: continue
                        )
                    ) ?: return isDropped
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


        val id = EmpirePlugin.empireItems._empireBlocksByData[MushroomBlockApi.getBlockData(e.block)]
		
        val listDrop: List<ItemDropManager.ItemDrop> = EmpirePlugin.dropManager.itemDrops[id?:block.blockData.material.name] ?: return
        if (dropItem(listDrop, block.location))
            e.isDropItems = false

    }


    @EventHandler
    fun onMobDeath(e: EntityDeathEvent) {
        val entity: Entity = e.entity
        val listDrop = EmpirePlugin.dropManager.mobDrops[entity.type.name] ?: return
        dropItem(listDrop, entity.location)
    }

    fun onDisable() {
        EntityDeathEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)

    }
}