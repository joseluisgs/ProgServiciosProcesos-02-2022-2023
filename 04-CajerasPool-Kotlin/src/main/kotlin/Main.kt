import models.Caja
import models.Cliente
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis


/**
 * Ejemplo de uso de hilos con Kotlin
 * Thread lanza hilos, podemos heredar de Hilos, o implementar
 * Runnable: para ejecutar código en un hilo
 * Callable: para ejecutar código en un hilo y devolver un valor
 * ExecutorService: para ejecutar hilos teniendo un pool de hilos fijos
 * y con ello optimizar recursos. Es decir, no creando hilos son razon!
 */

fun main() {
    println("Mis cajas \uD83D\uDED2")

    //hilosFijos()
    //executeWorkerRunnable()
    executeWorkerCallable()
}

fun executeWorkerCallable() {
    val NUM_HILOS = 4

    val clientes = mutableListOf<Cliente>()

    repeat((1..10).random()) {
        clientes.add(Cliente("Cliente ${it + 1}"))
    }
    println("Tenemos en cola a ${clientes.size} clientes")

    val executor = Executors.newFixedThreadPool(NUM_HILOS)
    var recaudacion = 0
    measureTimeMillis {
        val cajas = mutableListOf<Caja>()
        clientes.forEach {
            cajas.add(Caja(it))
        }
        val futures = executor.invokeAll(cajas)
        futures.forEachIndexed { index, future ->
            recaudacion += future.get()
            println("Caja: ${index + 1} factura un precio total de ${future.get()} €")
        }
        executor.shutdown()
    }.also {
        println("Tiempo total de atención total: $it ms")
        println("Recaudación total: $recaudacion €")
    }


}

fun executeWorkerRunnable() {
    val NUM_HILOS = 4

    // Queue of clients
    val clientes = mutableListOf<Cliente>()
    repeat((1..10).random()) {
        clientes.add(Cliente("Cliente ${it + 1}"))
    }
    println("Tenemos en cola a ${clientes.size} clientes")

    val executor = Executors.newFixedThreadPool(NUM_HILOS)
    measureTimeMillis {
        clientes.forEach {
            executor.execute(Caja(it))
        }
        executor.shutdown()
    }.also {
        println("Tiempo total de atención total: $it ms")
    }

}

private fun hilosFijos() {
    val NUM_HILOS = 4

    measureTimeMillis {
        val cajas = mutableListOf<Thread>()
        repeat(NUM_HILOS) {
            val cliente = Cliente("Cliente ${it + 1}")
            val caja = Thread(Caja(cliente))
            cajas.add(caja)
            caja.start()
        }

        cajas.forEach { it.join() }
    }.also {
        println("Tiempo total de procesamiento total: $it ms")
    }
}

var runnableTask = Runnable {
    try {
        TimeUnit.MILLISECONDS.sleep(300)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}

var callableTask: Callable<String> = Callable<String> {
    TimeUnit.MILLISECONDS.sleep(300)
    "Task's execution"
}