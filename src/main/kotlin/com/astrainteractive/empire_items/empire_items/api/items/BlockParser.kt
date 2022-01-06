package com.astrainteractive.empire_items.empire_items.api.items

import com.astrainteractive.astralibs.callSyncMethod
import com.astrainteractive.astralibs.catching
import net.minecraft.world.level.GeneratorAccess
import net.minecraft.world.level.block.state.IBlockData
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData
import java.lang.NumberFormatException

object BlockParser {

    private val blockList
        get() = mutableListOf<Material>(
            Material.BROWN_MUSHROOM_BLOCK,
            Material.RED_MUSHROOM_BLOCK,
            Material.MUSHROOM_STEM
        )

    private val facesMap
        get() = mapOf<String, Boolean>(
            BlockFace.DOWN.name.lowercase() to false,
            BlockFace.EAST.name.lowercase() to false,
            BlockFace.NORTH.name.lowercase() to false,
            BlockFace.SOUTH.name.lowercase() to false,
            BlockFace.UP.name.lowercase() to false,
            BlockFace.WEST.name.lowercase() to false
        )

    inline fun setTypeFast(block: Block, type: Material, facing: Map<String, Boolean> = mutableMapOf()){
        val oldCraftBlock = (block as CraftBlock)
        val position=oldCraftBlock.position
        val newData = type.createBlockData()
        catching {
            for (f in facing)
                ((newData as CraftBlockData) as MultipleFacing).setFace(BlockFace.valueOf(f.key.uppercase()), f.value)
        }
        val newCraftBlockData: IBlockData = (newData as CraftBlockData).state
        val generatorAccess = (oldCraftBlock.craftWorld.handle as GeneratorAccess)
        callSyncMethod{
            generatorAccess.a(position, newCraftBlockData, 1042,512)
            generatorAccess.minecraftWorld.a(position, oldCraftBlock.nms, newCraftBlockData, 3);
        }
    }

    fun getMultipleFacing(block: Block): MultipleFacing? {
        if (block.blockData !is MultipleFacing)
            return null
        if (!blockList.contains(block.type))
            return null
        return block.blockData as MultipleFacing
    }

    public fun getBlockData(block: Block): Int? {
        val facing = getMultipleFacing(block) ?: return null
        val data = getDataByFacing(facing) ?: return null
        return data + blockList.indexOf(block.type) * 64
    }

    private fun Boolean.toInt(): Int = if (this) 1 else 0

    private fun String.toIntOrNull(): Int? = try {
        this.toInt()
    } catch (e: NumberFormatException) {
        null
    }

    private fun Char.toBoolean() = (this == '1')
    private fun String.fixBinary(): String {
        var value = this
        for (i in 0 until facesMap.size - this.length)
            value = "0$value"
        return value
    }

    public fun getFacingByData(data: Int): Map<String, Boolean> {
        var d = data
        d -= (data / 64) * 64
        val faces = facesMap.toMutableMap()
        Integer.toBinaryString(d).fixBinary().forEachIndexed { i, c ->
            faces[facesMap.keys.elementAt(i)] = c.toBoolean()
        }
        return faces
    }

    public fun getMaterialByData(data: Int): Material {
        return blockList[data / 64]
    }

    private fun getDataByFacing(f: MultipleFacing): Int? {
        val byteLine = getMultipartByFacing(f).values.map { it.toInt() }.joinToString("")
        return byteLine.toIntOrNull(2)
    }

    private fun getMultipartByFacing(f: MultipleFacing): Map<String, Boolean> {

        val multipart = facesMap.toMutableMap()
        for (face in f.faces)
            multipart[face.name.lowercase()] = true
        return multipart

    }
}