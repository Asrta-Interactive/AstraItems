package com.astrainteractive.empire_items.api.models

import com.astrainteractive.astralibs.EmpireSerializer
import com.astrainteractive.astralibs.FileManager
import kotlinx.serialization.Serializable
val GUI_CONFIG: _GuiConfig
    get() = _GuiConfig.instance

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class _GuiConfig(
    val settings: Settings,
    val categories: Map<String, Category>,
) {
    companion object {
        lateinit var instance: _GuiConfig
            private set

        fun create(config:FileManager): _GuiConfig {
            val _config = EmpireSerializer.toClass<_GuiConfig>(config.getFile())
            instance = _config!!
            return instance
        }
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Settings(
        val titles: Titles,
        val buttons: Buttons,
        val sounds: Sounds,
    ) {

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class Titles(
            val workbenchText: String,
            val categoriesText: String,
            val soundsText: String,
        )

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class Sounds(
            val workbenchSound: String,
            val categoriesSound: String,
            val categorySound: String,
        )

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class Buttons(
            val moreButton: String,
            val nextButton: String,
            val prevButton: String,
            val backButton: String,
            val closeButton: String,
            val giveButton: String,
            val furnaceButton: String,
            val craftingTableButton: String,
        )
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Category(
        val id:String,
        val title: String,
        val name: String,
        val icon: String,
        val lore: List<String> = listOf(),
        val items: List<String>

    )
}