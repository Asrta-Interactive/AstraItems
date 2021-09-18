package com.makeevrserg.empireprojekt.empire_items.events.genericevents.drop

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI
import com.makeevrserg.empireprojekt.empire_items.api.MushroomBlockApi
import com.makeevrserg.empireprojekt.empire_items.events.genericevents.drop.data.ItemDrop
import com.makeevrserg.empireprojekt.empirelibs.IEmpireListener
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class ItemDropListener : IEmpireListener {

    private val blockLocations: MutableList<Location> = mutableListOf()




    private fun dropItem(list: List<ItemDrop>, l: Location): Boolean {
        var isDropped = false
        for (drop: ItemDrop in list) {
            val dropChance = Random.nextDouble(0.0, 100.0)
            if (drop.chance > dropChance) {
                isDropped = true
                for (i in 0 until Random.nextInt(drop.minAmount, drop.maxAmount + 1))
                    l.world?.dropItem(
                        l,
                        ItemsAPI.getEmpireItemStackOrItemStack(drop.id)?:continue
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



        val id =ItemsAPI.getEmpireBlockIdByData(MushroomBlockApi.getBlockData(e.block)?:return)
		
        val listDrop: List<ItemDrop> = EmpirePlugin.dropManager.itemDrops[id?:block.blockData.material.name] ?: return
        if (dropItem(listDrop, block.location))
            e.isDropItems = false

    }


    @EventHandler
    fun onMobDeath(e: EntityDeathEvent) {
        val entity: Entity = e.entity
        val listDrop = EmpirePlugin.dropManager.mobDrops[entity.type.name] ?: return
        dropItem(listDrop, entity.location)
    }

    override fun onDisable() {
        EntityDeathEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)

    }
}