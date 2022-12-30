package com.astrainteractive.empire_items

import com.astrainteractive.empire_itemss.api.EmpireItemsAPI
import com.astrainteractive.empire_itemss.api.items.BlockParser
import com.atrainteractive.empire_items.models.config.Config
import com.atrainteractive.empire_items.models.yml_item.YmlItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.block.BlockFace
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.IDependency
import ru.astrainteractive.astralibs.di.getValue
import kotlin.math.pow
import kotlin.random.Random

class BlockGeneratorAPI(
    empireItemsAPI: IDependency<EmpireItemsAPI>,
    config: IDependency<Config>,
    private val fastBlockPlacer: IFastBlockPlacer
) {
    private val empireItemsAPI by empireItemsAPI
    private val config by config
    private var currentChunkProcessing = 0L
    private val blocksToGenerate: List<YmlItem>
        get() = empireItemsAPI.itemYamlFilesByID.values.filter { it.block?.generate != null }
    private val dispatchers = BlockGenerationDispatchers

    /**
     * Получаем рандомное направление чтобы получить связный блок
     */
    private fun getRandomBlockFace(): BlockFace {
        val faces = BlockFace.values()
        return faces[Random.nextInt(faces.size)]
    }


    /**
     * Получение списка локация из чанка и добавление их в очередь
     */
    private suspend fun generateChunk(chunk: Chunk) {

        val map = blocksToGenerate.mapNotNull { itemInfo ->
            val block = itemInfo.block ?: return@mapNotNull null
            //Сгенерирован ли блок в чанке
            if (BlockGenerationHistory.isBlockGeneratedInChunk(chunk, itemInfo.id))
                return@mapNotNull null
            //Надо ли генерировать блок
            val generate = block.generate ?: return@mapNotNull null
            //Если указан мир и он не равен миру чанка - пропускаем
            if (generate.world != null && generate.world != chunk.world.name)
                return@mapNotNull null
            //Проверяем рандом
            BlockGenerationHistory.setChunkHasGenerated(chunk, itemInfo.id)
            if (generate.generateInChunkChance <= Random.nextDouble(0.0, 100.0))
                return@mapNotNull null


            //Получаем список локаций блоков по их типу
            val blockLocByType =
                BlockGenerationUtils.getBlocksLocations(chunk, generate.minY, generate.maxY, generate.replaceBlocks)
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
                            if (generatedPerDeposit > generate.maxPerDeposit)
                                return@mapNotNull null
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

        map.forEach {
            withContext(BlockGenerationDispatchers.blockGenerationPool) {
                val blocks = it.value.map { it.block }

                it.value.firstOrNull()?.let {
                    fastBlockPlacer.setTypeFast(it.material, it.facing, it.data, *blocks.toTypedArray())
                }
            }

        }
    }

    fun validateChunk(chunk: Chunk, isNewChunk: Boolean) {
        if (!isNewChunk && config.generation.onlyOnNewChunks)
            return

        if (!config.generation.enabled)
            return


        if (currentChunkProcessing >= config.generation.generateChunksAtOnce)
            if (config.generation.generateChunksAtOnce > 0)
                return
        PluginScope.launch (dispatchers.blockParsingPool) {
            withContext(dispatchers.generatorLauncherDispatcher) { currentChunkProcessing++ }
//            Logger.log(tag = "BlockGeneratorAPI", message = "generateChunk ${chunk}")
            generateChunk(chunk)
            withContext(dispatchers.generatorLauncherDispatcher) { currentChunkProcessing-- }
        }

    }
}
