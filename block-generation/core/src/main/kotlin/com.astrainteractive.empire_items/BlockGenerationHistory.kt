package com.astrainteractive.empire_items

import kotlinx.coroutines.withContext
import org.bukkit.Chunk
import ru.astrainteractive.astralibs.file_manager.FileManager
import java.io.File

object BlockGenerationHistory {
    val tempChunks: FileManager = FileManager("temp" + File.separator + "generated_chunks.yml")

    /**
     * Загружает в файл информацию о том, что чанк был сгенерирован
     */
    suspend fun setChunkHasGenerated(chunk: Chunk, id: String): Boolean =
        withContext(BlockGenerationDispatchers.fileHistoryScope.coroutineContext) {
            val tempChunks = tempChunks
            if (!tempChunks.fileConfiguration.contains("${chunk}.$id")) {
                tempChunks.fileConfiguration.set("${chunk}.$id", true)
                tempChunks.save()
            }
            true
        }

    suspend fun isBlockGeneratedInChunk(chunk: Chunk, id: String): Boolean =
        withContext(BlockGenerationDispatchers.fileHistoryScope.coroutineContext) {
            tempChunks.fileConfiguration.contains("${chunk}.$id")
        }


}