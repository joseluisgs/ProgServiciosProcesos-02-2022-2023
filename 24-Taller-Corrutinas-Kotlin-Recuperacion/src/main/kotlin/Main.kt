import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main(args: Array<String>): Unit = runBlocking {
    val lista1: List<Int> = List(5000) { (1..500).random() }
    val lista2: List<Int> = List(5000) { (1..500).random() }
    val lista3: List<Int> = List(5000) { (1..500).random() }


    // Puramente secuencial

    measureTimeMillis {
        burbuja(lista1)
    }.also { println("Tiempo de ejecución s1 lista 1: $it ms") }

    measureTimeMillis {
        burbuja(lista2)
    }.also { println("Tiempo de ejecución s2 lista 2: $it ms") }

    measureTimeMillis {
        burbuja(lista3)
    }.also { println("Tiempo de ejecución s3 lista 3: $it ms") }

    measureTimeMillis {
        burbuja(lista1)
        burbuja(lista2)
        burbuja(lista3)
    }.also { println("Tiempo de ejecución secuencial 3 listas: $it ms") }

    // Con parallelimos con corrutinas con Launch!
    val myScope = CoroutineScope(Dispatchers.Default)

    myScope.launch {
        measureTimeMillis {
            burbuja(lista1)
        }.also { println("Tiempo de ejecución c1 lista 1: $it ms") }
    }

    myScope.launch {
        measureTimeMillis {
            burbuja(lista2)
        }.also { println("Tiempo de ejecución c2 lista 2: $it ms") }
    }

    myScope.launch {
        measureTimeMillis {
            burbuja(lista3)
        }.also { println("Tiempo de ejecución c3 lista 3: $it ms") }
    }


    // pool.shutdown()
    println("Listo")

    // Con paralelismo con Future
    measureTimeMillis {
        val res01 = myScope.async { burbuja(lista1) }
        res01.await()
    }.also {
        println("Tiempo de ejecución f1 lista 1: $it ms")
    }

    withTimeoutOrNull(5000) {
        val res02 = myScope.async {
            measureTimeMillis {
                burbuja(lista2)
            }
        }
        res02.await()
    }?.also {
        println("Tiempo de ejecución f2 lista 2: $it ms")
    } ?: println("Tiempo de ejecución f2 lista 2: Timeout")


}

fun burbuja(lista: List<Int>): List<Int> {
    val listaOrdenada = lista.toMutableList()
    for (i in 0 until listaOrdenada.size) {
        for (j in 0 until listaOrdenada.size - 1) {
            if (listaOrdenada[j] > listaOrdenada[j + 1]) {
                val aux = listaOrdenada[j]
                listaOrdenada[j] = listaOrdenada[j + 1]
                listaOrdenada[j + 1] = aux
            }
        }
    }
    return listaOrdenada
}