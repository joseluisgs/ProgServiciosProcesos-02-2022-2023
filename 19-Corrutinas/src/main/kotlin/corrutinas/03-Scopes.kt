import kotlinx.coroutines.*

/**
 * Cada vez que usamos un constructor de coroutines en realidad estamos haciendo una llamada
 * a una función que recibe como primer parámetro un objeto de tipo CoroutineContext.
 * Los constructores launch y async son en realidad funciones de extensión de la interfaz CoroutineScope
 * Global scope: Es un scope general que se puede usar para cualquier corrutina
 * que deba continuar con la ejecución mientras la aplicación se está ejecutando.
 * Por lo tanto, no deben estar atados a ningún componente específico que pueda ser destruido.
 * Crear una coroutine valiéndose del objeto GlobalScope,
 * se asemeja a crear una coroutine con un Job no cancelable.
 * Es decir, la coroutine romperá la relación con el Scope donde fue creada y
 * vivirá hasta que la aplicación finalice su ejecución.
 * Por esta razón su uso se desaconseja a tal punto que solo debe hacerse
 * cuando sabes muy bien lo que estás haciendo.
 *
 * Los constructores runBlocking y coroutineScope pueden tener un aspecto similar
 * porque ambos esperan a que su cuerpo y todos sus elementos secundarios se completen.
 * La principal diferencia es que el método runBlocking bloquea el subproceso actual para esperar,
 * mientras que coroutineScope simplemente lo suspende, liberando el subproceso subyacente para otros usos.
 * Debido a esa diferencia, runBlocking es una función regular y coroutineScope es una función de suspensión.
 * Puede usar coroutineScope desde cualquier función de suspensión.
 * https://kotlinlang.org/docs/coroutines-basics.html#scope-builder
 *
 * Para crear funciones de suspensión solo debemos añadir la palabra suspend delante de la función
 */

// Debemos esperar que todo acabe para acabar main!!! Lo sacamos fuera
fun main() = runBlocking {
    // Asi se ejecutan en secuencial!!!

    // Intenta no usar GlobalScope
    doWorldGlobalScope()
    // // función de suspensión heredando el scope de la función main
    // Quedo atada al ciclo de vida de main
    doWorldHeredado1()
    doWorldHeredado2()
    val res = doAsynWorld()
    log("Resultado de doAsynWorld: $res")
    log("Fin! de manin")

    delay(3000)

    // Repetimos en paralelo y asincrono, para que veas su potencia!!!
    launch { doWorldGlobalScope() }
    launch { doWorldHeredado1() }
    launch { doWorldHeredado2() }
    val res2 = async { doAsynWorld() }
    launch { log("Resultado de doAsynWorld: ${res2.await()}") }
    log("Fin! de manin??")
}

@OptIn(DelicateCoroutinesApi::class)
fun doWorldGlobalScope() {

    GlobalScope.launch {
        delay(2000L)
        log("Global Scope Mundo 1!")
    }
    GlobalScope.launch {
        delay(1000L)
        log("Global Scope Mundo 2!")
    }
    log("Hola GlobalScope!")
}

// Hewredamos el scope de la funcion que nos llamma, así aplicamos concurrencia estructurada

suspend fun doWorldHeredado1() { // heredamos el contexto, no usamos GlobalScope
    coroutineScope {
        launch {
            delay(2000L)
            log("Heredado 1 Mundo 1!")
        }
        launch {
            delay(1000L)
            log("Heredado 1 Mundo 2!")
        }
        log("Hola Heredado 1!")
    }
}

// Podemos ponerlo si es toda la función!!
suspend fun doWorldHeredado2() = coroutineScope { // heredamos el contexto, no usamos GlobalScope
    launch {
        delay(2000L)
        log("Heredado 2 Mundo 1!")
    }
    launch {
        delay(1000L)
        log("Heredado 2 Mundo 2!")
    }
    log("Hola Hereado 2!")
}

suspend fun doAsynWorld(): Int = coroutineScope {
    val res = async {
        delay(2000L)
        log("Async Mundo 1!")
        1
    }
    val res2 = async {
        delay(1000L)
        log("Async Mundo 2!")
        2
    }
    log("Hola Async!")
    return@coroutineScope res.await() + res2.await()
}