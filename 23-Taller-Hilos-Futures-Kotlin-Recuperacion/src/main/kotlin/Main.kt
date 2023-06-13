import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
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

    // Con paralelismo con hilos
    val pool = Executors.newFixedThreadPool(4)

    pool.execute {
        measureTimeMillis {
            burbuja(lista1)
        }.also { println("Tiempo de ejecución h1 lista 1: $it ms") }
    }

    pool.execute {
        measureTimeMillis {
            burbuja(lista2)
        }.also { println("Tiempo de ejecución h2 lista 2: $it ms") }
    }

    pool.execute {
        measureTimeMillis {
            burbuja(lista3)
        }.also { println("Tiempo de ejecución h3 lista 3: $it ms") }
    }


    // pool.shutdown()
    println("Listo")

    // Con paralelismo con Future
    val future1 = pool.submit(Callable {
        burbuja(lista1)
    })


    val res = future1.get(1000, TimeUnit.MILLISECONDS)
    println("Resultado de la tarea f1: ${res.take(10)}")

    pool.shutdown()


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