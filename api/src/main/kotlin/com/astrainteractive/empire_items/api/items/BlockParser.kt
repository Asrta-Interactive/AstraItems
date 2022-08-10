package com.astrainteractive.empire_items.api.items

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.utils.catching
import net.minecraft.world.level.GeneratorAccess
import net.minecraft.world.level.block.state.IBlockData
import net.minecraft.world.level.block.state.properties.BlockStateBoolean
import net.minecraft.world.level.block.state.properties.IBlockState
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_19_R1.block.impl.CraftHugeMushroom

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

    @Suppress("UNCHECKED_CAST")
    fun <T, K> getDeclaredField(clazz: Class<T>, name: String): K? = catching(true) {
        clazz.getDeclaredField(name).run {
            isAccessible = true
            val field = this.get(null)
            isAccessible = false
            field as? K?
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T, K> getField(clazz: Class<T>, name: String): K? = catching(true) {
        clazz.getField(name).run {
            isAccessible = true
            val field = this.get(null)
            isAccessible = false
            field as? K?
        }
    }

    fun <T, K> setDeclaredField(clazz: Class<T>, instance: Any, name: String, value: K?) = catching(true) {
        clazz.getDeclaredField(name).run {
            isAccessible = true
            set(instance, value)
            isAccessible = false
        }

    }

    fun setTypeFast(block: Block, type: Material, facing: Map<String, Boolean> = emptyMap(), blockData: Int? = null) =
        setTypeFast(listOf(block), type, facing, blockData)

    fun setTypeFast(
        blocks: List<Block>,
        type: Material,
        facing: Map<String, Boolean> = emptyMap(),
        blockData: Int? = null
    ) {
        val newData = type.createBlockData().apply {
            val craftBlockData = this as CraftBlockData
            val craftHugeMushroom = craftBlockData as MultipleFacing as CraftHugeMushroom
            val FACES: Array<BlockStateBoolean> = getDeclaredField(craftHugeMushroom::class.java, "FACES")!!
            setDeclaredField(
                craftHugeMushroom.javaClass.superclass,
                craftHugeMushroom,
                "parsedStates",
                null as Map<IBlockState<*>, Comparable<*>>?
            )
            for (f in facing) {
                val face = BlockFace.valueOf(f.key.uppercase())
                val state = FACES[face.ordinal]
                val newState = (craftHugeMushroom.state as IBlockData).a(state, f.value)
                setDeclaredField(craftHugeMushroom.javaClass.superclass, craftHugeMushroom, "state", newState)
            }
        }

        AsyncHelper.callSyncMethod {
            blocks.forEach { block ->
                val world = ((block as CraftBlock).craftWorld.handle as GeneratorAccess)
                val position = (block as CraftBlock).position
                val old = world.a_(position);
                val blockData: IBlockData = (newData as CraftBlockData).state
                world.a(position, blockData, 1042);
                world.minecraftWorld.a(position, old, blockData, 3)
            }
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