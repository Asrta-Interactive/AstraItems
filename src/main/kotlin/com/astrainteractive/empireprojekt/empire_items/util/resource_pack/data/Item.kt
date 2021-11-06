package com.astrainteractive.empireprojekt.empire_items.util.resource_pack.data

import com.google.gson.JsonObject


data class Model(
    var parent: String,
    var textures: Textures,
    var overrides: MutableList<ModelOverrite>?,
    var gui_light: String?,
    var display: JsonObject?
) {
    public fun sort() {
        overrides ?: return
        overrides?.sortBy { it.predicate.custom_model_data }
    }
}

data class ModelOverrite(
    val model: String,
    val predicate: Predicate
)

data class Predicate(

    val custom_model_data: Int,
    val blocking: Int?,
    val pulling: Int?,
    val pull: Double?
)

data class Textures(
    var layer0: String?,
    var layer1: String?,
    var all: String?,
    var particle: String?
)
