fun main() {
    println("El infierno es la asincronia - Callback")

    callbackDemo()
}

/**
 * Callback es una funcion que se ejecuta cuando se termina de ejecutar otra funcion
 * pasamos la funcion como parametro
 * De esta manera podemos ejecutar una funcion despues de que se ejecute otra
 * y usamos la asincronía
 * Lo único que sabemos es que se ejecutará después de que se ejecute la función
 */
fun callbackDemo() {
    val callback = {
        logger.debug("Callback ejecutado")
        (1..10).random()
    }
    val callback2 = {
        logger.debug("Callback2 ejecutado")
        Thread.sleep(2000)
        69
    }

    // Ejecutamos la funcion con el callback

    doCallback(callback)
    doCallback(callback2)

    doCallback {
        logger.debug("Callback3 ejecutado")
        "Callback3".length
    }

    doCallback {
        logger.debug("Callback4 ejecutado")
        anotherCallback("Pepe") {
            logger.debug("Another Callback ejecutado")
            3
        }
    }

}

fun doCallback(callback: () -> Int) {
    // Hacemos algo
    logger.debug("Doing something Callback")
    Thread.sleep(1000)
    val res = callback() // Ejectuamos el callback
    println("res: $res")
}

fun anotherCallback(cadena: String, callback: () -> Int): Int {
    logger.debug("Doing something AnotherCallback")
    Thread.sleep(2000)
    println("Cadena: $cadena")
    return callback() // Ejecutamos el callback
}
