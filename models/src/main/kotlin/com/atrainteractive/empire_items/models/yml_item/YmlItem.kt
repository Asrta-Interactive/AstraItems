package com.atrainteractive.empire_items.models.yml_item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@kotlinx.serialization.Serializable
data class YmlItem(
    val id: String,
    val displayName: String,
    val lore: List<String> = listOf(),
    val material: String,
    @SerialName("texturePath")
    val _texturePath: String? = null,
    @SerialName("modelPath")
    val _modelPath: String? = null,
    val customModelData: Int = 0,
    val itemFlags: List<String> = listOf(),
    val namespace: String = "empire_items",
    @SerialName("empire_enchants")
    val empireEnchants: Map<String, String> = mapOf(),
    val enchantments: Map<String, Int> = mapOf(),
    val durability: Int? = null,
    val armorColor: String? = null,
    val attributes: Map<String, Double> = mapOf(),
    val customTags: List<String> = listOf(),
    val block: Block? = null,
    val musicDisc: Interact.PlaySound? = null,
    val interact: Map<String, Interact> = mapOf(),
    val gun: Gun? = null,
    val decoration: Decoration? = null,
    val book: Book? = null
) {
    val texturePath: String?
        get() = _texturePath?.replace(".png", "")
    val modelPath: String?
        get() = _modelPath?.replace(".json", "")

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Book(
        val title: String,
        val author: String,
        val pages: Map<String, List<String>>
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class Decoration(
        val placeSound: String,
        val breakSound: String,
        val placeParticle: Interact.PlayParticle,
        val breakParticle: Interact.PlayParticle,
    )
}