package com.astrainteractive.empire_items.api

import com.astrainteractive.empire_items.api.utils.IManager
import kotlin.math.abs
import kotlin.math.sign

object FontApi : IManager {
    override suspend fun onDisable() {
    }

    override suspend fun onEnable() {
    }

    fun playerFonts() = EmpireItemsAPI.fontByID.filter { !it.value.blockSend }

    enum class HudOffsets(val offset: Int, val char: String) {
        LEFT_1(-1, "\uF801"),
        LEFT_2(-2, "\uF802"),
        LEFT_3(-3, "\uF803"),
        LEFT_4(-4, "\uF804"),
        LEFT_5(-5, "\uF805"),
        LEFT_6(-6, "\uF806"),
        LEFT_7(-7, "\uF807"),
        LEFT_8(-8, "\uF808"),
        LEFT_16(-16, "\uF809"),
        LEFT_32(-32, "\uF80A"),
        LEFT_64(-64, "\uF80B"),
        LEFT_128(-128, "\uF80C"),
        LEFT_512(-512, "\uF80D"),
        LEFT_1024(-1024, "\uF80E"),
        RIGHT_1(1, "\uF821"),
        RIGHT_2(2, "\uF822"),
        RIGHT_3(3, "\uF823"),
        RIGHT_4(4, "\uF824"),
        RIGHT_5(5, "\uF825"),
        RIGHT_6(6, "\uF826"),
        RIGHT_7(7, "\uF827"),
        RIGHT_8(8, "\uF828"),
        RIGHT_16(16, "\uF829"),
        RIGHT_32(32, "\uF82A"),
        RIGHT_64(64, "\uF82B"),
        RIGHT_128(128, "\uF82C"),
        RIGHT_512(512, "\uF82D"),
        RIGHT_1024(1024, "\uF82E");


        companion object {
            private fun nearestAndSmaller(value: Int): HudOffsets? {
                val sign = value.sign
                return values().filter { it.offset.sign == sign }.filter { abs(it.offset) <= abs(value) }
                    .maxByOrNull { abs(it.offset) }
            }

            fun getOffsets(_offset: Int): String {
                val isEven = _offset % 2
                var offset = _offset - isEven
                var stringOffset = nearestAndSmaller(isEven)?.char ?: ""
                var nearest = nearestAndSmaller(offset)
                while (nearest != null) {
                    stringOffset += nearest.char
                    offset -= nearest.offset
                    nearest = nearestAndSmaller(offset)
                }
                return stringOffset
            }
        }


    }

    fun getOffsets() = mapOf(
        "l_1" to "\uF801",
        "l_2" to "\uF802",
        "l_3" to "\uF803",
        "l_4" to "\uF804",
        "l_5" to "\uF805",
        "l_6" to "\uF806",
        "l_7" to "\uF807",
        "l_8" to "\uF808",
        "l_16" to "\uF809",
        "l_32" to "\uF80A",
        "l_64" to "\uF80B",
        "l_128" to "\uF80C",
        "l_512" to "\uF80D",
        "l_1024" to "\uF80E",
        "r_1" to "\uF821",
        "r_2" to "\uF822",
        "r_3" to "\uF823",
        "r_4" to "\uF824",
        "r_5" to "\uF825",
        "r_6" to "\uF826",
        "r_7" to "\uF827",
        "r_8" to "\uF828",
        "r_16" to "\uF829",
        "r_32" to "\uF82A",
        "r_64" to "\uF82B",
        "r_128" to "\uF82C",
        "r_512" to "\uF82D",
        "r_1024" to "\uF82E"
    )
}