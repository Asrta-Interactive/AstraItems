import kotlin.system.measureTimeMillis
import kotlin.test.Test

class Tests {
    private fun calculation() {
        var j = 0
        for (i in 0 until 10000) {
            j += i
        }
    }

    fun firstMethod() {
        val xz = (0 until 15) zip (0 until 15)
        xz.flatMap { (x, z) ->
            (0 until 255).mapNotNull { y ->
                calculation()
            }
        }.shuffled()
    }
    fun secondMethod() {
        (0 until 15).flatMap {x->
            (0 until 15).flatMap {z->
                (0 until 255).mapNotNull { y ->
                    calculation()
                }
            }

        }.shuffled()
    }

    @Test
    fun test() {
        measureTimeMillis {
            firstMethod()
        }.also { println("First method: ${it/1000.0}") }
        measureTimeMillis {
            secondMethod()
        }.also { println("Second method: ${it/1000.0}") }

    }
}