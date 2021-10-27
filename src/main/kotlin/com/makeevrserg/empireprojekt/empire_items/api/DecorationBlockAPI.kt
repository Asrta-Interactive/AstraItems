package com.makeevrserg.empireprojekt.empire_items.api

import com.makeevrserg.empireprojekt.EmpirePlugin
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.asEmpireItem
import com.makeevrserg.empireprojekt.empire_items.api.ItemsAPI.getEmpireID
import com.makeevrserg.empireprojekt.items.data.decoration.Decoration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import kotlin.math.abs
import kotlin.math.floor

object DecorationBlockAPI {

    private fun decorationByID(id: String) = ItemsAPI.getEmpireItemInfo(id)?.decoration


    fun checkForBoundingBox(decoration: Decoration, location: Location): Boolean {
//        val l = location.clone()
//        if (l.block.type != Material.AIR)
//            return false
        return true
    }

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
//        breakBoundingBox(location.add(0.0, 1.0, 0.0))
//        breakBoundingBox(location.add(0.0, -1.0, 0.0))

    }

    fun rotateBlock(location: Location) {
        val decor = getDecorationByBoundingBox(location) ?: return
        if (decor !is ItemFrame)
            return
        val itemFrame = decor as ItemFrame
        val rotation = itemFrame.rotation
        val index = Rotation.values().indexOf(rotation)+1
        val newRotation = Rotation.values().elementAtOrNull(index)?:Rotation.values().first()
        itemFrame.rotation = newRotation
    }
    fun breakItem(location: Location) {
        val decor = getDecorationByBoundingBox(location) ?: return
        if (decor !is ItemFrame)
            return
        val itemFrame = decor as ItemFrame
        val item = itemFrame.item.getEmpireID()
        val itemStack = item.asEmpireItem()?.clone() ?: return
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

    fun placeBlock(id: String, location: Location, playerLoc: Location, rotate: Boolean = false): Boolean {
        val decoration = decorationByID(id) ?: return false
        val itemStack = id.asEmpireItem()?.clone() ?: return false
        if (!checkForBoundingBox(decoration, location))
            return false

        location.block.type = Material.AIR


        val itemFrame = location.world!!.spawnEntity(location.clone().floor(), EntityType.ITEM_FRAME) as ItemFrame
        itemFrame.isFixed = true
        itemFrame.isVisible = false
        itemFrame.itemDropChance = 0.0f
        itemFrame.isCustomNameVisible = false
        itemFrame.isInvulnerable = true
        itemFrame.customName = ""
        if (playerLoc.pitch > 0)
            itemFrame.setFacingDirection(BlockFace.UP, true)
        else
            itemFrame.setFacingDirection(BlockFace.DOWN, true)


        val yaw = playerLoc.yaw
        fun getRotation(r: Int) = (abs(yaw - r) / 45).toInt() == 0


        val fotateMap = mapOf(
            0 to BlockFace.NORTH,
            90 to BlockFace.EAST,
            180 to BlockFace.SOUTH,
            -180 to BlockFace.SOUTH,
            -90 to BlockFace.WEST
        )

        if (rotate)
            listOf(0, 90, 180, -180, -90).forEach {
                if (getRotation(it))
                    itemFrame.setFacingDirection(fotateMap[it] ?: return@forEach)
            }


        val flipMap = mapOf(
            0 to Rotation.FLIPPED,
            45 to Rotation.FLIPPED_45,
            90 to Rotation.COUNTER_CLOCKWISE,
            135 to Rotation.COUNTER_CLOCKWISE_45,
            180 to Rotation.NONE,
            -45 to Rotation.CLOCKWISE_135,
            -90 to Rotation.CLOCKWISE,
            -135 to Rotation.CLOCKWISE_45
        )
        if (!rotate)
            listOf(0, 45, 90, 135, 180, -45, -90, -135).forEach {
                if (getRotation(it))
                    itemFrame.rotation = flipMap[it] ?: return@forEach
            }

        val meta = itemStack.itemMeta
        meta?.setDisplayName(null)
        itemStack.itemMeta = meta
        itemFrame.setItem(itemStack)
        createBoundingBox(decoration, location)
        return true
    }
}