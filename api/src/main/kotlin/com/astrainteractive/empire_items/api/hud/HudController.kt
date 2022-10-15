package com.astrainteractive.empire_items.api.hud

import com.astrainteractive.empire_items.api.FontApi
import com.astrainteractive.empire_items.api.models.FontImage
import kotlin.math.pow
import kotlin.math.sign

object HudController {
    //    fun findNearestOffset(amount:Int): FontApi.HudOffset? {
//        val answer = FontApi.HudOffset.values().fold(null) { acc: Int?, hudOffset ->
//            val offsetValue = hudOffset.offset
//            if (offsetValue <= amount && (acc == null || offsetValue > acc)) offsetValue
//            else acc
//        }
//        return FontApi.HudOffset.values().firstOrNull { it.offset==answer }
//    }
    fun findExactHudOffset(value: Int): FontApi.HudOffset? {
        return FontApi.HudOffset.values()
            .firstOrNull { it.offset == value }
    }

    fun destructUntilFound(value: Int): List<FontApi.HudOffset> {
        var times = 0
        var offset: FontApi.HudOffset? = null
        while (offset == null) {
            times++

            offset = findExactHudOffset(value / (2.0.pow(times)).toInt())
        }
        return IntRange(0,times).map { offset }
    }

    fun offsets(value: Int): List<FontApi.HudOffset> {
        println("Find offsets for $value")
        val half = value / 2
        val remainder = half % 2
        return buildList {
            addAll(destructUntilFound(half))
            addAll(destructUntilFound(remainder))
        }
    }

    private fun fontOffset(amount: Int): String {
        return offsets(amount).joinToString("") { it.char }
    }

    private fun fontWithOffset(font: FontImage): String {
        val size = font.height
        val halfOffset = fontOffset(-size / 2)
        val remainedOffset = fontOffset(-size % 2)
        return remainedOffset + halfOffset + font.char + halfOffset + fontOffset(-1)
    }

    fun addFontElement(line: String, font: FontImage, offset: Int): String {
        val minusDiv2 = fontOffset(-offset / 2)
        val plusDiv2 = fontOffset(offset / 2)
        return minusDiv2 + plusDiv2 + line + plusDiv2 + fontWithOffset(font) + minusDiv2
    }

    fun build(vararg elements: PlayerHud): String {
        var line = ""
        val sortedFonts = elements.sortedBy { it.xPosition }
        sortedFonts.forEach {
            line = addFontElement(line, it.astraFont, it.xPosition)
        }
        return line
    }

}