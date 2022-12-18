package com.astrainteractive.empire_items

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material

object BlockGenerationUtils {
    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    fun getBlocksLocations(
        chunk: Chunk,
        yMin: Int,
        yMax: Int,
        chanceByType: Map<String, Int>
    ): List<Pair<String, Location>> {
        val materialByType = chanceByType.mapKeys { Material.getMaterial(it.key) }
        val locationsByMaterial = getMaterialLocations(chunk, yMin, yMax)
        return materialByType.flatMap {
            val material = it.key ?: return@flatMap emptyList()
            val materialLocations = locationsByMaterial[material.ordinal]
            materialLocations.map { l ->
                material.name.uppercase() to l
            }
        }.shuffled()
    }

    private fun getMaterialLocations(chunk: Chunk, minY: Int, maxY: Int): Array<MutableList<Location>> {
        val amount: Array<MutableList<Location>> = Array(Material.values().size) { mutableListOf() }
        for (y in minY..maxY) {
            for (x in 0 until 15) {
                for (z in 0 until 15) {
                    val globalX = chunk.x shl 4 or x
                    val globalZ = chunk.z shl 4 or z
                    val list = amount[chunk.getBlock(x, y, z).type.ordinal]
                    list.add(Location(chunk.world, globalX.toDouble(), y.toDouble(), globalZ.toDouble()))
                }
            }
        }
        return amount
    }
}