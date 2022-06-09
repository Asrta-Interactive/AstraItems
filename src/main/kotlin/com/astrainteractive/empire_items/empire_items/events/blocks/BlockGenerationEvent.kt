package com.astrainteractive.empire_items.empire_items.events.blocks


import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.EmpirePlugin
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.util.Files
import com.astrainteractive.empire_items.empire_items.util.TriplePair
import com.astrainteractive.empire_items.empire_items.util.calcChance
import com.astrainteractive.empire_items.models.CONFIG
import com.astrainteractive.empire_items.models.yml_item.YmlItem
import kotlinx.coroutines.*
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.world.ChunkLoadEvent
import kotlin.random.Random

class BlockGenerationEvent {
    private val TAG: String
        get() = "BlockGenerationEvent"
    private var currentChunkProcessing = 0L

    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    private fun Chunk.getBlocksLocations(
        yMin: Int,
        yMax: Int,
        types: Map<String, Int>
    ): List<Pair<String, Location>> {
        val xZShuffled = (0 until 15).shuffled() zip (0 until 15).shuffled()
        return (yMin until yMax).shuffled().flatMap { y ->
            return@flatMap xZShuffled.map { (x, z) ->
                return@map TriplePair(x, y, z)
            }
        }.mapNotNull { (x, y, z) ->
            val loc = Location(world, this.x * 16.0 + x, y * 1.0, this.z * 16.0 + z)
            val blockName = loc.block.type.name
            val chance = types[blockName] ?: return@mapNotNull null
            if (calcChance(chance))
                return@mapNotNull Pair(blockName, loc)
            return@mapNotNull null
        }

    }

    /**
     * Загружает в файл информацию о том, что чанк был сгенерирован
     */
    private fun setChunkHasGenerated(chunk: Chunk, id: String) = synchronized(this) {
        val tempChunks = Files.tempChunks
        if (!tempChunks.getConfig().contains("${chunk}.$id")) {
            tempChunks.getConfig().set("${chunk}.$id", true)
            tempChunks.saveConfig()
        }
    }

    private inline fun isBlockGeneratedInChunk(chunk: Chunk, id: String): Boolean = synchronized(this) {
        return Files.tempChunks.getConfig().contains("${chunk}.$id")
    }

    /**
     * Получаем рандомное направление чтобы получить связный блок
     */
    fun getRandomBlockFace(): BlockFace {
        val faces = BlockFace.values()
        return faces[Random.nextInt(faces.size)]
    }

    /**
     * Заменяем блок на сгенерированный
     */
    private fun replaceBlock(id: String, location: Location, material: String, faces: Map<String, Boolean>) = AsyncHelper.launch {
        delay(1000)
        if (CONFIG.generation.debug)
            log("Creating ${id} at {${location.x}; ${location.y}; ${location.z}}")
        BlockParser.setTypeFast(location.block, Material.getMaterial(material) ?: return@launch, faces)
    }

    private val blocksToGenerate: List<YmlItem>
        get() = EmpireItemsAPI.itemYamlFilesByID.values.filter { it.block?.generate != null }

    /**
     * Получение списка локация из чанка и добавление их в очередь
     */
    private fun generateChunk(chunk: Chunk) {
        blocksToGenerate.forEach { itemInfo ->
            val block = itemInfo.block ?: return@forEach
            //Сгенерирован ли блок в чанке
            if (isBlockGeneratedInChunk(chunk, itemInfo.id))
                return@forEach
            //Надо ли генерировать блок
            val generate = block.generate ?: return@forEach
            //Если указан мир и он не равен миру чанка - пропускаем
            if (block.generate.world != null && block.generate.world != chunk.world.name)
                return@forEach
            //Проверяем рандом
            if (generate.generateInChunkChance < Random.nextDouble(100.0)) {
                setChunkHasGenerated(chunk, itemInfo.id)
                return@forEach
            }


            //Получаем список локаций блоков по их типу
            val blockLocByType =
                chunk.getBlocksLocations(
                    generate.minY ?: return@forEach,
                    generate.maxY ?: return@forEach,
                    generate.replaceBlocks ?: return@forEach
                )

            if (blockLocByType.isEmpty())
                return@forEach

            val material = BlockParser.getMaterialByData(block.data)
            val facing = BlockParser.getFacingByData(block.data)
            //Количество сгенерированных блоков
            var generated = 0
            blockLocByType.forEach block@{ (_, location) ->
                if (generated > generate.maxPerChunk)
                    return@block
                var faceBlock = location.block
                val originalBlockType = faceBlock.type
                val depositAmount = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit + 1)
                for (unnamed in 0 until depositAmount) {
                    if (generated > generate.maxPerChunk)
                        return@block
                    for (i in 0 until 10) {
                        val newFaceBlock = faceBlock.getRelative(getRandomBlockFace())
                        if (newFaceBlock.type == originalBlockType) {
                            faceBlock = newFaceBlock
                            break
                        }
                    }

                    replaceBlock(itemInfo.id, faceBlock.location.clone(), material.name, facing)
                    setChunkHasGenerated(chunk, itemInfo.id)

                    generated++
                }

            }
        }
    }


    val chunkLoadEvent = DSLEvent.event(ChunkLoadEvent::class.java) { e ->
        val chunk = e.chunk
        if (!e.isNewChunk && CONFIG.generation.onlyOnNewChunks)
            return@event
        if (!CONFIG.generation.enabled)
            return@event

        if (currentChunkProcessing >= CONFIG.generation.generateChunksAtOnce)
            return@event
        currentChunkProcessing++
        AsyncHelper.launch {
            generateChunk(chunk)
            currentChunkProcessing--
        }

    }


    private fun log(message: String) = Logger.log(message, TAG)
}

