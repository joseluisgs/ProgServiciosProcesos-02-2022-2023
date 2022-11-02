import kotlinx.coroutines.*
import mu.KotlinLogging

val logger = KotlinLogging.logger {}

suspend fun main() {
    println("El infierno es la asincronia - Async Await")
    asyncAwaitDemo()
}

suspend fun asyncAwaitDemo() {
    // Defino un scope o lo heredo
    val myScope = CoroutineScope(Dispatchers.IO)

    val async1 = myScope.async {
        logger.debug { "lanzando async 1" }
        doAsyncAwait1()
    }
    val async2 = myScope.async {
        logger.debug { "lanzando async 2" }
        delay(1000)
        doAsyncAwait2()
    }
    val async3 = myScope.async {
        logger.debug { "lanzando async 3" }
        doAsyncAwait3("Esto es una corrutina")
    }
    val async4 = myScope.async {
        logger.debug { "lanzando async 4" }
        doAsyncAwait4().await()
    }

    // Recogemos los valores
    println("res1: ${async1.await()}")
    println("res2: ${async2.await()}")
    println("res3: ${async3.await()}")
    println("res4: ${async4.await()}")

}


suspend fun doAsyncAwait1(): Int {
    // Hacemos algo
    logger.debug { "Doing something doAsyncAwait1" }
    delay(1000) // Es como un sleep pero suspende la ejecucion de la funcion
    return (1..10).random()
}

fun doAsyncAwait2(): Int {
    logger.debug { "Doing something doAsyncAwait2" }
    Thread.sleep(2000) // No usar sleep en corrutinas, porque te cargas su ventaja!!
    return 69
}

suspend fun doAsyncAwait3(cadena: String): Int {
    logger.debug { "Doing something doAsyncAwait3" }
    delay(1000)
    println("Cadena: $cadena")
    return cadena.length
}

suspend fun doAsyncAwait4() = coroutineScope {
    async {
        logger.debug { "Doing something doAsyncAwait4" }
        delay(1000)
        doAsyncAwait5()
    }
}

suspend fun doAsyncAwait5(): Long {
    logger.debug { "Doing something doAsyncAwait5" }
    delay(1000)
    return System.currentTimeMillis()
}



