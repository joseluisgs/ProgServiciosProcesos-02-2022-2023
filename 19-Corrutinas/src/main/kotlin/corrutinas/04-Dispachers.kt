import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Los dispatchers son un tipo de contextos de corrutina que especifican el hilo o hilos
 * que pueden ser utilizados por la corrutina para ejecutar su código.
 * Hay dispatchers que solo usan un hilo (como Main) y otros que definen un grupo de hilos
 * que se optimizarán para ejecutar todas las corrutinas que reciben.
 * Si recuerdas, al principio dijimos que 1 hilo puede ejecutar muchas corrutinas,
 * por lo que el sistema no creará 1 hilo por corrutina, sino que intentará reutilizar los que ya están vivos.
 * Tenemos cuatro dispatchers principales:
 * - Default: Se usará cuando no se defina un dispatcher, pero también podemos configurarlo explícitamente.
 * Este dispatcher se utiliza para ejecutar tareas que hacen un uso intensivo de la CPU,
 * principalmente cálculos de la propia App, algoritmos, etc.
 * Puede usar tantos subprocesos como cores tenga la CPU.
 * Ya que estas son tareas intensivas, no tiene sentido tener más ejecuciones al mismo tiempo,
 * porque la CPU estará ocupada.
 * -IO: Utiliza este para ejecutar operaciones de entrada/salida. En general,
 * todas las tareas que bloquearán el hilo mientras esperan la respuesta de otro sistema:
 * peticiones al servidor, acceso a la base de datos, sitema de archivos, sensores…
 * ya que no usan la CPU, se puede tener muchas en ejecución al mismo tiempo,
 * por lo que el tamaño de este grupo de hilos es de 64.
 * Las Apps lo que más hacen, es interactuar con el dispositivo y hacer peticiones de red,
 * por lo que probablemente usarás este la mayoría del tiempo.
 * - Unconfined: Si no te importa mucho qué hilo se utiliza, puedes usar este dispatcher.
 * Es difícil predecir qué hilo se usará, así que no lo uses a menos que estés muy seguro
 * de lo que estás haciendo.
 * Main: Este es un dispatcher especial que se incluye en las librerías de corrutinas relacionadas
 * con interfaz de usuario. En particular, en Android, utilizará el hilo de UI.
 *
 * Puedo usarlo en el CoroutineScope para especificar el dispatcher que quiero usar.
 * O donde los builders de corrutinas, como launch, async, etc.
 *
 * https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html#dispatchers-and-threads
 */

fun main() = runBlocking<Unit> {
    // Main
    launch { // context of the parent, main runBlocking coroutine
        log("main runBlocking: Mi hilo es ${Thread.currentThread().name}")
    }

    // Nocconfinado... puede ser cualquiera y mejor evitar
    launch(Dispatchers.Unconfined) {
        log("Unconfined: Mi hilo es ${Thread.currentThread().name}")
    }

    // Default, para tareas intensivas de cálculo o algoritmos
    launch(Dispatchers.Default) {
        log("Default: Mi hilo es ${Thread.currentThread().name}")
    }

    // IO, para tareas de entrada/salida
    launch(Dispatchers.IO) {
        log("IO: Mi hilo es ${Thread.currentThread().name}")
    }

    // En mi propio hilo que yo elija
    launch(newSingleThreadContext("MiPropioHilo")) { // will get its own new thread
        log("newSingleThreadContext:Mi hilo es${Thread.currentThread().name}")
    }

    launch { // will get its own
        conAsync()
    }
}

// Con WithContext decimos que dispacher o grupos de hilos van a responsabilizarse...
// Si no, lo hará por defecrto de quien la llame...
// Yo prefiero usar el withContext si lo tengo muy claro
// O asignarselo yo cuando lo llame!!
suspend fun doSomethingUsefulOne(): Int = withContext(Dispatchers.IO) {
    // Imaginemos un login o acceso a base de datos
    log("doSomethingUsefulOne")
    delay(1000L) // pretend we are doing something useful here
    13
}

suspend fun doSomethingUsefulTwo(): Int = coroutineScope {
    // Imaginemos una consulta a api rest
    log("doSomethingUsefulTwo")
    delay(1000L) // pretend we are doing something useful here, too
    29
}

suspend fun conAsync() = coroutineScope {
    log("con Async")
    val time = measureTimeMillis {
        // Por defecto vamos a usar este contexto con este dispacher!!
        val myScope = CoroutineScope(Dispatchers.Default)
        val myScope2 = CoroutineScope(Dispatchers.IO)
        val myScope3 = CoroutineScope(newSingleThreadContext("MiPropioHiloMetodo"))
        // async lanza en paralelo, pero devuleve un resultado...
        // Pero lo podemos cambiar!!!
        val one = myScope.async { doSomethingUsefulOne() }
        val two = myScope2.async { doSomethingUsefulTwo() }
        val three = myScope3.async { doSomethingUsefulTwo() }

        log("La respuesta es ${one.await() + two.await() + three.await()}")
    }

    log("completado en $time ms")
}
