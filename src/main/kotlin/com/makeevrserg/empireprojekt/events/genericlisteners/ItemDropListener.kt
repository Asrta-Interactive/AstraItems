package com.makeevrserg.empireprojekt.events.genericlisteners

import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class ItemDropListener : Listener {

    data class ItemDrop(
        val dropFrom: String,
        val item: String,
        val minAmount: Int,
        val maxAmount: Int,
        val chance: Double
    )

    private var mobDrops: MutableMap<String, List<ItemDrop>>
    private var itemDrops: MutableMap<String, List<ItemDrop>>
    private val blockLocations: MutableList<Location> = mutableListOf()

    var everyDropByItem: MutableMap<String, MutableList<ItemDrop>> = mutableMapOf()

    private fun initDrop(section: ConfigurationSection?): MutableMap<String, List<ItemDrop>> {
        section ?: return mutableMapOf()
        val drop: MutableMap<String, List<ItemDrop>> = mutableMapOf()

        for (entityKey in section.getKeys(false)) {
            val list: MutableList<ItemDrop> = mutableListOf()
            for (item in section.getConfigurationSection(entityKey)!!.getKeys(false)) {
                val itemSect = section.getConfigurationSection(entityKey)!!.getConfigurationSection(item)!!
                list.add(
                    ItemDrop(
                        entityKey,
                        item,
                        itemSect.getInt("min_amount", 0),
                        itemSect.getInt("max_amount", 0),
                        itemSect.getDouble("chance", 0.0)
                    )
                )

            }
            drop[entityKey] = list
        }

        return drop

    }

    private fun initEveryDrop() {
        val map: MutableMap<String, List<ItemDrop>> = mutableMapOf()
        map.putAll(mobDrops)
        map.putAll(itemDrops)
        for (key in map.keys) {
            for (drop in map[key]!!) {
                if (everyDropByItem[drop.item] == null)
                    everyDropByItem[drop.item] = mutableListOf()
                everyDropByItem[drop.item]!!.add(drop)
            }
        }
    }

    init {
        itemDrops = initDrop(plugin.empireFiles.dropsFile.getConfig()?.getConfigurationSection("loot.blocks"))
        mobDrops = initDrop(plugin.empireFiles.dropsFile.getConfig()?.getConfigurationSection("loot.mobs"))
        initEveryDrop()
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun dropItem(list: List<ItemDrop>, l: Location) {
        for (drop: ItemDrop in list) {
            if (drop.chance > Random.nextInt(0, 100))
                for (i in 0 until Random.nextInt(drop.minAmount, drop.maxAmount + 1))
                    l.world?.dropItem(
                        l,
                        plugin.empireItems.empireItems[drop.item] ?: ItemStack(
                            Material.getMaterial(drop.item) ?: continue
                        )
                    )?:return
        }
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


        val listDrop: List<ItemDrop> = itemDrops[block.blockData.material.name] ?: return
        dropItem(listDrop, block.location)
    }


    @EventHandler
    fun onMobDeath(e: EntityDeathEvent) {
        val entity: Entity = e.entity
        val listDrop = mobDrops[entity.type.name] ?: return
        dropItem(listDrop, entity.location)
    }

    fun onDisable() {
        EntityDeathEvent.getHandlerList().unregister(this)
        BlockBreakEvent.getHandlerList().unregister(this)

    }
}