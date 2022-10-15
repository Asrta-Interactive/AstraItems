package com.astrainteractive.empire_items.empire_items.events.blocks


import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.empire_items.api.EmpireItemsAPI
import com.astrainteractive.empire_items.api.items.BlockParser
import com.astrainteractive.empire_items.empire_items.events.blocks.BlockGenerationEventUtils.getBlocksLocations
import com.astrainteractive.empire_items.empire_items.util.Files
import com.astrainteractive.empire_items.api.models.CONFIG
import com.astrainteractive.empire_items.api.models.yml_item.YmlItem
import com.astrainteractive.empire_items.api.utils.calcChance
import kotlinx.coroutines.*
import net.minecraft.core.BlockPosition
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_19_R1.CraftChunk
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock
import org.bukkit.event.world.ChunkLoadEvent
import ru.astrainteractive.astralibs.utils.AstraEstimator
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.random.Random


object BlockGenerationEventUtils {

    /**
     * Получаем список координат блоков, которые необходимо будет заменить
     */
    fun Chunk.getBlocksLocations(
        yMin: Int,
        yMax: Int,
        chanceByType: Map<String, Int>
    ): List<Pair<String, Location>> {
        val craftChunk = (this as CraftChunk)
        return (0 until 15).flatMap { x ->
            (0 until 15).flatMap { z ->
                (yMin until yMax).mapNotNull { y ->
//                    val block = this.getBlock(x,y,z)
                    val block = CraftBlock(
                        craftChunk.craftWorld.handle,
                        BlockPosition(this.x shl 4 or x, y, this.z shl 4 or z)
                    ) as Block
                    val chance = chanceByType[block.type.name] ?: return@mapNotNull null
                    if (calcChance(chance))
                        Pair(block.type.name, block.location)
                    else null
                }
            }
        }.shuffled()
    }

}

class BlockGenerationEvent {
    private val TAG: String
        get() = "BlockGenerationEvent"
    private var currentChunkProcessing = 0L

    private val scope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))

    private val blockGenerationPool = newFixedThreadPoolContext(4, "blockGenerationPool")
    private val blockParsingPool = newFixedThreadPoolContext(4, "blockParsingPool")

    /**
     * Загружает в файл информацию о том, что чанк был сгенерирован
     */
    private suspend fun setChunkHasGenerated(chunk: Chunk, id: String): Boolean = withContext(scope.coroutineContext) {
        val tempChunks = Files.tempChunks
        if (!tempChunks.fileConfiguration.contains("${chunk}.$id")) {
            tempChunks.fileConfiguration.set("${chunk}.$id", true)
            tempChunks.save()
        }
        true
    }

    private suspend fun isBlockGeneratedInChunk(chunk: Chunk, id: String): Boolean =
        withContext(scope.coroutineContext) {
            Files.tempChunks.fileConfiguration.contains("${chunk}.$id")
        }

    /**
     * Получаем рандомное направление чтобы получить связный блок
     */
    private fun getRandomBlockFace(): BlockFace {
        val faces = BlockFace.values()
        return faces[Random.nextInt(faces.size)]
    }

    private val blocksToGenerate: List<YmlItem>
        get() = EmpireItemsAPI.itemYamlFilesByID.values.filter { it.block?.generate != null }

    class MeanTimeCalculator(private val tag: String, private val onEvery: Int = 50) {
        private var amount: Int = 0
        private var time: Long = 0
        fun onAnother(time: Long) {
            this.time += time
            amount++
            if (amount % onEvery == 0) {
                Logger.log("${time / amount.toDouble() / 1000.0}", tag)
            }
        }

        fun <T> calculate(block: () -> T): T {
            var value: T? = null
            val time = AstraEstimator.invoke {
                value = block()
            }
            onAnother(time)
            return value!!
        }
    }

    val calculator = MeanTimeCalculator("blockLoc")

    class BlockInfo(
        val block: Block,
        val material: Material,
        val data: Int,
        val facing: Map<String, Boolean>
    )

    /**
     * Получение списка локация из чанка и добавление их в очередь
     */
    private suspend fun generateChunk(chunk: Chunk) {

        val map = blocksToGenerate.mapNotNull { itemInfo ->
            val block = itemInfo.block ?: return@mapNotNull null
            //Сгенерирован ли блок в чанке
            if (isBlockGeneratedInChunk(chunk, itemInfo.id))
                return@mapNotNull null
            //Надо ли генерировать блок
            val generate = block.generate ?: return@mapNotNull null
            //Если указан мир и он не равен миру чанка - пропускаем
            if (block.generate!!.world != null && block.generate!!.world != chunk.world.name)
                return@mapNotNull null
            //Проверяем рандом
            setChunkHasGenerated(chunk, itemInfo.id)
            if (!calcChance(generate.generateInChunkChance))
                return@mapNotNull null


            //Получаем список локаций блоков по их типу
            val blockLocByType = chunk.getBlocksLocations(generate.minY, generate.maxY, generate.replaceBlocks)
                .ifEmpty { return@mapNotNull null }


            val material = BlockParser.getMaterialByData(block.data)
            val facing = BlockParser.getFacingByData(block.data)

            //Количество сгенерированных блоков
            var generatedPerChunk = 0

            blockLocByType.mapNotNull block@{ (_, location) ->
                var generatedPerDeposit = 0
                if (generatedPerChunk > generate.maxPerChunk)
                    return@block null

                var faceBlock = location.block
                val originalBlockType = faceBlock.type

                val depositAmount = Random.nextInt(generate.minPerDeposit, generate.maxPerDeposit)
                val range = IntRange(0, depositAmount.toDouble().pow(1 / 3.0).toInt())
                range.flatMap { x ->
                    range.flatMap { y ->
                        range.mapNotNull { z ->
                            if (generatedPerChunk > generate.maxPerChunk)
                                return@mapNotNull null
                            if (generatedPerDeposit>generate.maxPerDeposit)
                                return@mapNotNull  null
                            val newFaceBlock = faceBlock.getRelative(x, y, z)
                            if (newFaceBlock.type != originalBlockType) return@mapNotNull null
                            generatedPerChunk++
                            generatedPerDeposit++
                            val blockToReplace = newFaceBlock.location.block
                            BlockInfo(
                                blockToReplace,
                                Material.getMaterial(material.name)!!,
                                block.data,
                                facing
                            )
                        }
                    }
                }
            }
        }.flatten().flatten().groupBy { it.data }
        if (CONFIG.generation.debug) {
            val map = map.mapNotNull {
                it.value.firstOrNull()?.let {
                    val it = it.block
                    "(${it.location.x.toInt()}; ${it.location.y.toInt()}; ${it.location.z.toInt()})"
                }
            }
            if (map.isEmpty())
                Logger.log("Generated list is empty")
            else
                Logger.log("${map}")
        }
        map.forEach {
            PluginScope.launch(blockGenerationPool) {
                val blocks = it.value.map { it.block }

                it.value.firstOrNull()?.let {
                    BlockParser.setTypeFast(blocks, it.material, it.facing, it.data)
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
            if (CONFIG.generation.generateChunksAtOnce > 0)
                return@event
        currentChunkProcessing++
        PluginScope.launch(blockParsingPool) {
            generateChunk(chunk)
            currentChunkProcessing--
        }

    }


    private fun log(message: String) = Logger.log(message, TAG)
}

