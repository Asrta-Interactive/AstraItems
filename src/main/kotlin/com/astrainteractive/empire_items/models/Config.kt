package com.astrainteractive.empire_items.models

import com.astrainteractive.empire_items.empire_items.util.EmpireSerializer
import com.astrainteractive.empire_items.empire_items.util.Files
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val CONFIG: _Config
    get() = _Config.instance

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class _Config(
    @SerialName("resource_pack")
    val resourcePack: ResourcePackConfig,
    val generation: GenerationConfig,
    val tabPrefix: String
) {
    companion object {
        lateinit var instance: _Config
            private set

        fun create(): _Config {
            val _config = EmpireSerializer.toClass<_Config>(Files.configFile.getFile())
            instance = _config!!
            return instance
        }
    }


    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class GenerationConfig(
        val debug: Boolean,
        val enabled: Boolean,
        val onlyOnNewChunks: Boolean,
        val generateChunksAtOnce: Int,

        )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class ResourcePackConfig(
        val link: String,
        val requestOnJoin: Boolean,
        val kickOnDeny: Boolean,
        val requestDelay: Int
    )

}
