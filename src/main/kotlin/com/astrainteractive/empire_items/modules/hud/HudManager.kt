package com.astrainteractive.empire_items.modules.hud

import com.astrainteractive.empire_items.empire_items.api.font.AstraFont
import com.astrainteractive.empire_items.empire_items.api.font.FontManager

object HudManager {

    private fun fontOffset(amount: Int): String {
        return FontManager.HudOffsets.getOffsets(amount)
    }

    private fun fontWithOffset(font: AstraFont): String {
        val size = font.height
        val offset = fontOffset(-size / 2)
        return fontOffset(-size % 2) + offset + font.char + offset + fontOffset(-1)
    }

    fun addFontElement(line: String, font: AstraFont, offset: Int): String {
        val minusDiv2 = fontOffset(-offset / 2)
        val plusDiv2 = fontOffset(offset / 2)
        return minusDiv2 + plusDiv2 + line + plusDiv2 + fontWithOffset(font) + minusDiv2

    }

}