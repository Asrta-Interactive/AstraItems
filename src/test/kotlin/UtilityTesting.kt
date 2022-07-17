import org.junit.Test

class UtilityTesting {
    @Test
    fun craftingShapeTest() {
        val range = IntRange(0, 53)
        val arr = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        val res = arr.mapIndexed { i, it ->
            (i/3+1)*9+1+i%3+1
        }
        IntRange(0,2).map{x->
            IntRange(0,2).map {y->
                println("$x; $y")
            }
        }
        println(res)
    }
}