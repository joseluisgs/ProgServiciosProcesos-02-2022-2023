import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

fun main() {
    println("El infierno es la asincronia - Future")
    futureDemo()
}

fun futureDemo() {
    val pool = Executors.newFixedThreadPool(4)

    // Puedo pasarle al pool un Callable o un Runnable, callable si devuelve un valor
    // Lo mejor s tengo una lista de tareas y quiero que se ejecuten todas
    val future1 = pool.submit(Callable {
        logger.debug { "lanzando future 1" }
        Thread.sleep(1000)
        doFuture1()
    })
    val future2 = pool.submit(Callable {
        logger.debug { "lanzando future 2" }
        doFuture2()
    })
    val future3 = pool.submit(Callable {
        logger.debug { "lanzando future 3" }
        doFuture3()
    })

    val future4 = pool.submit(Callable {
        logger.debug { "lanzando future 4" }
        Thread.sleep(1000)
        doFuture4()?.get() ?: throw Exception("No se ha podido ejecutar el future4")
    })

    // La ventaja de hacerlo así es que no necesitamos el pool explicitamente -- La mejor en Java
    val future5 = CompletableFuture.supplyAsync {
        logger.debug { "lanzando future 5" }
        doFuture5("Soy una promesa")
    }

    // Obtenemos el rsultado
    println("future1: ${future1.get()}")
    println("future2: ${future2.get()}")
    println("future3: ${future3.get()}")
    println("future4: ${future4.get()}")
    println("future5: ${future5.get()}")

    pool.shutdown() // Paramos el pool

}

fun doFuture1(): Int {
    // Hacemos algo
    logger.debug { "Doing something doFuture1" }
    Thread.sleep(1000)
    return (1..10).random()
}

fun doFuture2(): Int {
    // Hacemos algo
    logger.debug { "Doing something doFuture2" }
    Thread.sleep(2000)
    return 69
}

fun doFuture3(): String {
    // Hacemos algo
    logger.debug { "Doing something doFuture3" }
    Thread.sleep(1000)
    return doFuture4()?.get().toString()
}

fun doFuture4(): CompletableFuture<Int>? {
    // Propio pool implicito
    val localFuture = CompletableFuture.supplyAsync {
        logger.debug { "Doing something doFuture4" }
        Thread.sleep(1000)
        (1..10).random()
    }
    return localFuture
}

fun doFuture5(cadena: String): Int {
    logger.debug { "Doing something doFuture5" }
    Thread.sleep(2000)
    println("Cadena: $cadena")
    return cadena.length
}
