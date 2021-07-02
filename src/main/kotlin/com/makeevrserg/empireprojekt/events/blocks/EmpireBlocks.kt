package com.makeevrserg.empireprojekt.events.blocks

import com.destroystokyo.paper.profile.PlayerProfile
import com.earth2me.essentials.Essentials
import com.makeevrserg.empireprojekt.EmpirePlugin.Companion.plugin
import com.makeevrserg.empireprojekt.util.EmpireUtils
import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.scheduler.BukkitTask
import java.util.*

class EmpireBlocks() : Listener {


    @EventHandler
    public fun blockPlaceEvent(e: BlockPlaceEvent) {
    }


    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    public fun onDisable() {
        BlockPlaceEvent.getHandlerList().unregister(this)

    }
}