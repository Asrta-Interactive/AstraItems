package com.astrainteractive.empire_items.api.items

import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.EmpireItemsAPI.empireID
import com.astrainteractive.empire_items.api.EmpireItemsAPI.toAstraItemOrItem
import com.astrainteractive.empire_items.api.models.yml_item.YmlItem
import com.astrainteractive.empire_items.api.utils.playSound
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Rotation
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemFrame
import kotlin.math.floor
import kotlin.math.sign

object DecorationBlockAPI {

    private fun Location.floor() = Location(
        world,
        floor(x),
        floor(y),
        floor(z)
    )

    private fun getDecorationByBoundingBox(location: Location): Entity? {
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


    private fun breakBoundingBox(location: Location) {
        if (location.block.type != Material.BARRIER)
            return
        location.block.type = Material.AIR
    }

    private fun createBoundingBox(decoration: YmlItem.Decoration, location: Location): Boolean {
        val l = location.clone()
        l.block.type = Material.BARRIER
        return true
    }

    fun breakItem(location: Location) {
        val decor = getDecorationByBoundingBox(location) ?: return
        if (decor !is ItemFrame)
            return

        val itemFrame = decor as ItemFrame
        val item = itemFrame.item.empireID
        val itemStack = item.toAstraItemOrItem()?.clone() ?: return
        val decoration = EmpireItemsAPI.itemYamlFilesByID[item] ?: return
        location.playSound(decoration.decoration?.breakSound)
        location.world?.dropItem(location, itemStack)
        itemFrame.setItem(null)
        itemFrame.remove()
        breakBoundingBox(location)
    }


    fun placeBlock(id: String, itemFrame: ItemFrame, playerLoc: Location, blockFace: BlockFace): Boolean {
        val decoration = EmpireItemsAPI.itemYamlFilesByID[id]?.decoration ?: return false
        itemFrame.location.playSound(decoration.placeSound)
        val itemStack = id.toAstraItemOrItem()?.clone() ?: return false

        itemFrame.isFixed = true
        itemFrame.isVisible = false
        itemFrame.itemDropChance = 0.0f
        itemFrame.isCustomNameVisible = true
        itemFrame.isInvulnerable = true
        itemFrame.customName = ""
        var yaw = playerLoc.yaw + 10 * (playerLoc.yaw.sign)
        if (blockFace == BlockFace.DOWN)
            yaw *= -1
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
        itemFrame.rotation = flipMap[(yaw / 45).toInt()]!!
        val meta = itemStack.itemMeta
        meta?.setDisplayName(null)
        itemStack.itemMeta = meta
        itemFrame.setItem(itemStack)
        createBoundingBox(decoration, itemFrame.location)
        return true
    }
}