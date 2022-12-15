import com.astrainteractive.empire_items.util.MoreReflectedUtil
import java.lang.reflect.Field
import kotlin.test.Test
import kotlin.test.assertEquals

class Tests {
    @Test
    fun test(){
        val javaClass = JavaClass("b")
        assertEquals(javaClass.b(),"b")
        println("javaClass: ${javaClass.b()}")
        MoreReflectedUtil.setFinalField(javaClass,"c","b",true)
        println("javaClass: ${javaClass.b()}")
        assertEquals(javaClass.b(),"c")


    }
}