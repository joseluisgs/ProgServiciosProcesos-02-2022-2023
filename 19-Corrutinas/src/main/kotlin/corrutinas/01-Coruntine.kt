import kotlinx.coroutines.*
import mu.KotlinLogging
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 * Una corrutina es un hilo ligero que se ejecuta en un hilo de JVM
 * y que puede ser suspendida y reanudada en cualquier momento.
 * Las corrutinas son muy eficientes y no consumen muchos recursos
 * de la JVM.
 *
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/
 *
 * Su secreto es que no son hilos de verdad, sino que son un
 * mecanismo de programación que permite ejecutar código de forma
 * asíncrona y sin bloquear el hilo principal.
 * https://kotlinlang.org/docs/coroutines-overview.html
 * https://doordash.engineering/wp-content/uploads/2021/11/coroutine-11-1-1024x484.jpg
 *
 * En el contexto de concurrencia, bloquear un hilo significa que el hilo se mantendrá fuera de uso mientras
 * éste encuentre algo que lo bloquee. Ésto podría ser la espera de la liberación de un recurso,
 * una llamada a la función Thread.sleep, una llamada a un servicio externo, etc.
 * Mientras se encuentre en ese estado, el hilo no podrá ser usado para realizar otras tareas.
 * Por el contrario, suspender un hilo significa que el hilo estará libre y listo para ser usado
 * en la ejecución de otras tareas mientras se encuentra a la espera de la liberación de un recurso,
 * una llamada a la función delay, una llamada a un servicio externo, etc.
 * La naturaleza de las coroutines es suspender la ejecución evitando a toda costa llamadas
 * a funciones que bloquean. Ésta es la magia que le permite a las coroutines ser
 * tan eficientes y de bajo consumo de recursos en comparación con los hilos regulares de siempre.
 *
 * Las funciones de suspensión tienen la capacidad de suspender la ejecución de la corrutina
 * mientras están haciendo su trabajo. Una vez que termina, el resultado de la operación se devuelve
 * y se puede utilizar en la siguiente línea.
 */

private val logger = KotlinLogging.logger {}

fun main() = runBlocking {

    //hilos()
    //coroutines()


    // bloquea el hilo principal hasta que termine la corrutina

    /*launch { // lanza una corrutina en segundo plano y continua con el hilo principal
        delay(1000L) // suspende la corrutina durante 1 segundo no bloquea
        println("Hola") // imprime en el hilo principal
        log("Hola") // imprime en el hilo principal
    }*/

    println("corrutinas") // imprime en el hilo principal
    log("corrutinas") // imprime en el hilo principal
    //testThread()
    println()
    testCoroutine()

}

fun hilos() {
    println("¡Hilos!")
    val time = measureTimeMillis {
        // Una lista de 15_000 hilos, prueba con menos para ver la diferencia
        // Cada hilo que creas se corresponde con un hilo del SO
        // Si un hilo se queda esperando, el SO no puede hacer nada con él, queda inutilizado. Bloqueado
        val threads = List(15_000) {
            thread {
                // Los suspendemos 5 segundo ... Va a cascar
                logger.debug { "(${Thread.currentThread().name}) : voy a dormir y es bloqueante" }
                sleep(5000)
                log('.')
                logger.debug { "(${Thread.currentThread().name}) : me desperté" }
            }
        }
        // Esperamos a que terminen
        threads.forEach {
            it.join()
        }
        logger.debug { "¡Terminado!" }
    }
    // Medimos el tiempo
    log("Time: $time")
}

// El nombre de runBlocking significa que el hilo que lo ejecuta (en este caso, el hilo principal) se bloquea durante la duración de la llamada,
// hasta que todas las corrutinas dentro de runBlocking {...} completen su ejecución. No se debe usar a la ligera lo veremos más adelante
fun coroutines() = runBlocking {
    println("¡Corrutinas!")
    val time = measureTimeMillis {
        // Una lista de 10_000 coroutines
        val coroutines = List(150_000) {
            // un constructor de corrutinas. Lanza una nueva corrutina al mismo tiempo que el resto del código,
            // que continúa funcionando de forma independiente.
            launch {
                // es una función de suspensión especial. Suspende la corrutina durante un tiempo específico.
                // La suspensión de una corrutina no bloquea el subproceso subyacente,
                // por lo tanto el hilo que ejecuta la corrutina puede continuar ejecutando otras corrutinas.
                // esto permite que otras corrutinas se ejecuten.
                // cuando la corrutina se reanuda, se reanuda desde el punto en que se suspendió. Por el mismo hilo u otro
                logger.debug { "(${Thread.currentThread().name}) : voy a suspender y no es bloqueante" }
                delay(5000)
                log('.')
                logger.debug { "(${Thread.currentThread().name}) : me desperté" }
            }
        }
        // Esperamos que terminen
        coroutines.forEach {
            it.join()
        }
        log('\n')
    }

    log("Time: $time")
}

// Cada hilo que creas se corresponde con un hilo del SO
// Si un hilo se queda esperando, el SO no puede hacer nada con él, queda inutilizado. Bloqueado
// Se recupera una vez que termina la operación
fun testThread() {
    log("Java thread Sleep")

    repeat(10) {
        thread {
            log("Antes de la ejecución $it: ${Thread.currentThread().name}")
            log("Hi  Number  ${it}: ${Thread.currentThread().name}")
            sleep(100)
            log("Después de la ejecución $it: ${Thread.currentThread().name}")
        }
    }
}

// Con las corrutinas reutilizamos hilos y una vez suspendamos libreramos, y cuando se reanude se reanuda desde el punto en que se suspendió
// Por el mismo hilo u otro
@OptIn(DelicateCoroutinesApi::class)
suspend fun testCoroutine() {
    log("Kotlin coroutine Delay: ")
    repeat(10) {
        GlobalScope.launch {
            log("Antes de la ejecución $it: ${Thread.currentThread().name}")
            log("Hi  Number  ${it}: ${Thread.currentThread().name}")
            delay(100)
            log("Después de la ejecución $it: ${Thread.currentThread().name}")
        }
    }
    runBlocking {
        delay(2000)
    }
}