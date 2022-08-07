package com.astrainteractive.empire_items.modules.hud

import com.astrainteractive.empire_items.api.FontApi
import com.astrainteractive.empire_items.api.models.FontImage

object HudManager {

    private fun fontOffset(amount: Int): String {
        return FontApi.HudOffsets.getOffsets(amount)
    }

    private fun fontWithOffset(font: FontImage): String {
        val size = font.height
        val offset = fontOffset(-size / 2)
        return fontOffset(-size % 2) + offset + font.char + offset + fontOffset(-1)
    }

    fun addFontElement(line: String, font: FontImage, offset: Int): String {
        val minusDiv2 = fontOffset(-offset / 2)
        val plusDiv2 = fontOffset(offset / 2)
        return minusDiv2 + plusDiv2 + line + plusDiv2 + fontWithOffset(font) + minusDiv2

    }

}