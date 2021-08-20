package com.makeevrserg.empireprojekt.empire_items.events.blocks

import com.google.gson.annotations.SerializedName
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import java.lang.NumberFormatException

class MushroomBlockApi {


    data class Apply(val model:String?)
    data class Multipart(
        @SerializedName("when")
        var facing: Map<String, Boolean> = getFaceMap(),
        var apply: Apply? = null

    ) {

        operator fun get(key: String): Boolean? {
            return facing[key]
        }

        operator fun set(key: String, value: Boolean) {
            val map = facing.toMutableMap()
            map[key] = value
            facing = map
        }
        operator fun set(index: Int, value: Boolean) {
            val map = facing.toMutableMap()
            val key = map.keys.elementAt(index)

            map[key] = value
            facing = map
        }

        companion object {
            public fun getFaceMap(): Map<String, Boolean> = mapOf<String, Boolean>(
                BlockFace.DOWN.name.lowercase() to false,
                BlockFace.EAST.name.lowercase() to false,
                BlockFace.NORTH.name.lowercase() to false,
                BlockFace.SOUTH.name.lowercase() to false,
                BlockFace.UP.name.lowercase() to false,
                BlockFace.WEST.name.lowercase() to false
            )
        }
    }

    companion object {
        val blockList =
            mutableListOf<Material>(Material.BROWN_MUSHROOM_BLOCK,Material.RED_MUSHROOM_BLOCK,Material.MUSHROOM_STEM)

        public fun getMultipleFacing(block: Block): MultipleFacing? {
            if (block.blockData !is MultipleFacing)
                return null
            if (!blockList.contains(block.type))
                return null

            return block.blockData as MultipleFacing
        }




        public fun getBlockData(block: Block): Int? {
            val facing = getMultipleFacing(block) ?: return null
            val data = getDataByFacing(facing) ?: return null
            return data + blockList.indexOf(block.type)*64

        }

        private fun Boolean.toInt(): Int = if (this) 1 else 0

        private fun String.toIntOrNull(): Int? = try {
            this.toInt()
        } catch (e: NumberFormatException) {
            null
        }
        private fun Char.toBoolean() = (this=='1')
        private fun String.fixBinary(): String {
            var value = this
            for (i in 0 until Multipart.getFaceMap().size-this.length)
                value = "0$value"
            return value
        }
        public fun getFacingByData(data:Int): Multipart {
            var d = data
            d -= (data / 64) * 64
            val multipart = Multipart()
            val byteString = Integer.toBinaryString(d).fixBinary()
            for (i in byteString.indices) {
                val char = byteString[i]
                multipart[i] = char.toBoolean()
            }
            return multipart

        }
        public fun getMaterialByData(data:Int): Material {
            return blockList[data/64]
        }

        private fun getDataByFacing(f: MultipleFacing): Int? {
            val mPart = getMultipartByFacing(f)
            var byteLine = ""
            for (part in mPart.facing)
                byteLine += "${part.value.toInt()}"
            return byteLine.toIntOrNull(2)
        }

        private fun getMultipartByFacing(f: MultipleFacing): Multipart {
            val multipart = Multipart()
            for (face in f.faces)
                multipart[face.name.lowercase()] = true
            return multipart
        }
    }
}