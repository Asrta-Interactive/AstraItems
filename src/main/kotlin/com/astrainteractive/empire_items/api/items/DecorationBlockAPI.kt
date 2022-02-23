package com.astrainteractive.empire_items.api.items

import com.astrainteractive.empire_items.api.items.data.ItemApi
import com.astrainteractive.empire_items.api.items.data.ItemApi.getAstraID
import com.astrainteractive.empire_items.api.items.data.ItemApi.toAstraItemOrItem
import com.astrainteractive.empire_items.api.items.data.decoration.Decoration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import kotlin.math.floor
import kotlin.math.sign

object DecorationBlockAPI {

    private fun decorationByID(id: String) = ItemApi.getItemInfo(id)?.decoration


    fun Location.floor() = Location(
        world,
        floor(x),
        floor(y),
        floor(z)
    )

    fun getDecorationByBoundingBox(location: Location): Entity? {
        val l = location.clone().floor()
        for (e in location.world?.getNearbyEntities(location, 2.0, 2.0, 2.0) ?: return null) {
            if (e !is ItemFrame)
                continue
            val el = e.location.clone().floor()
            if (l == el)
                return e
        }
        return null
    }


    fun breakBoundingBox(location: Location) {
        if (location.block.type != Material.BARRIER)
            return
        location.block.type = Material.AIR
    }


    fun breakItem(location: Location) {
        val decor = getDecorationByBoundingBox(location) ?: return
        if (decor !is ItemFrame)
            return
        val itemFrame = decor as ItemFrame
        val item = itemFrame.item.getAstraID()
        val itemStack = item.toAstraItemOrItem()?.clone() ?: return
        location.world?.dropItem(location, itemStack)
        itemFrame.setItem(null)
        itemFrame.remove()
        breakBoundingBox(location)
    }

    fun createBoundingBox(decoration: Decoration, location: Location): Boolean {
        val l = location.clone()
        l.block.type = Material.BARRIER
        return true
    }

    fun placeBlock(id: String, location: Location, playerLoc: Location): Boolean {
        val decoration = decorationByID(id) ?: return false
        val itemStack = id.toAstraItemOrItem()?.clone() ?: return false
        location.block.type = Material.AIR


        val itemFrame = location.world!!.spawnEntity(location.clone().floor(), EntityType.ITEM_FRAME) as ItemFrame
        itemFrame.isFixed = true
        itemFrame.isVisible = true
        itemFrame.itemDropChance = 0.0f
        itemFrame.isCustomNameVisible = false
        itemFrame.isInvulnerable = true
        itemFrame.customName = ""
        if (playerLoc.pitch > 0)
            itemFrame.setFacingDirection(BlockFace.UP, true)
        else
            itemFrame.setFacingDirection(BlockFace.DOWN, true)


        val yaw = playerLoc.yaw+10*(playerLoc.yaw.sign)

        val flipMap = mapOf(
            0 to Rotation.FLIPPED,
            1 to Rotation.FLIPPED_45,
            2 to Rotation.COUNTER_CLOCKWISE,
            3 to Rotation.COUNTER_CLOCKWISE_45,
            4 to Rotation.NONE,
            -4 to Rotation.NONE,
            -3 to Rotation.CLOCKWISE_45,
            -2 to Rotation.CLOCKWISE,
            -1 to Rotation.CLOCKWISE_135
        )
        itemFrame.rotation = flipMap[(yaw/45).toInt()]!!


        val meta = itemStack.itemMeta
        meta?.setDisplayName(null)
        itemStack.itemMeta = meta
        itemFrame.setItem(itemStack)
        createBoundingBox(decoration, location)
        return true
    }
}