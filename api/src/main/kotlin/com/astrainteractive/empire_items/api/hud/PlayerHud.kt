package com.astrainteractive.empire_items.api.hud

import com.astrainteractive.empire_items.api.models.FontImage

data class PlayerHud(
    val id: String,
    val xPosition: Int,
    val astraFont: FontImage
)