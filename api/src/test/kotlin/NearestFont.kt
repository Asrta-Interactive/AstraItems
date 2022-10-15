import com.astrainteractive.empire_items.api.FontApi
import com.astrainteractive.empire_items.api.hud.HudController
import kotlin.test.Test
import kotlin.test.assertEquals

class NearestFont {
    @Test
    fun HudOffsetAllValues() {
        FontApi.HudOffset.values().forEach {
            val nearest = HudController.findExactHudOffset(it.offset)
            assertEquals(nearest, it)
        }
    }

    @Test
    fun destructionTest() {
        HudController.destructUntilFound(10).also {
            assertEquals(it, listOf(FontApi.HudOffset.RIGHT_5, FontApi.HudOffset.RIGHT_5))

        }
        HudController.destructUntilFound(12).also {
            assertEquals(it, listOf(FontApi.HudOffset.RIGHT_6, FontApi.HudOffset.RIGHT_6))

        }
    }
//    @Test
//    fun HudOffsetCustom(){
//        IntRange(-12,-8).forEach {
//            val nearest = HudController.nearestAndSmaller(it)
//            assertEquals(nearest, FontApi.HudOffset.LEFT_8)
//        }
//
//        IntRange(-16,-13).forEach {
//            val nearest = HudController.nearestAndSmaller(it)
//            assertEquals(nearest, FontApi.HudOffset.LEFT_16)
//        }
//    }
}