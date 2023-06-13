import kotlinx.coroutines.*

/**
 * Las corrutinas siguen un principio de concurrencia estructurada, lo que significa
 * que las nuevas corrutinas solo se pueden lanzar en un CoroutineScope específico que delimita
 * la vida útil de la corrutina.
 * El ejemplo anterior muestra que runBlocking establece el alcance correspondiente y
 * es por eso que el ejemplo anterior espera hasta que World!
 * se imprime después de un segundo de retraso y solo entonces sale.
 * En una aplicación real, lanzará muchas corrutinas. La simultaneidad estructurada
 * garantiza que no se pierdan ni se filtren.
 * Un alcance externo no puede completarse hasta que se completen todas sus rutinas secundarias.
 * La concurrencia estructurada también garantiza que cualquier error en el código
 * se informe correctamente y nunca se pierda.
 *
 * https://kotlinlang.org/docs/coroutines-basics.html#structured-concurrency
 *
 * Existen varios constructores de coroutines, cada uno para un caso específico.
 * Estos constructores son: runBlocking, launch, async y produce.
 * También es posible crear coroutines dentro de otra coroutine sin ninguna limitación.
 * Por lo tanto, una coroutine puede tener muchas coroutines “hijas”, y
 * éstas a su vez pueden tener más coroutines “hijas” y así infinitamente.
 */

fun main() {
    ejemploRunBlocking()
    ejemploLaunch()
    ejemploAsync()
}


/**
 * Éste es un caso especial de constructor. Crea una coroutine y bloquea el hilo que lo ejecuta
 * hasta que la coroutine finalice, es decir, bloquea el hilo actual hasta que se terminen
 * todas las tareas dentro de esa corrutina.
 * Este constructor no debe ser usado nunca, excepto para hacer pruebas unitarias de nuestras suspend functions.
 * También es posible usarlo en el método main para asegurarnos que termina cuando termine la última corrutina
 * NUNCA utilices este constructor de coroutines en código de producción. Debido a que runBlocking no
 * es una función de extensión de la interfaz CoroutineScope, se puede usar en el interior de cualquier función.
 * Lo que en realidad pasa al crear una coroutine con runBlocking es que el hilo que la crea esperará bloqueado a que la
 * coroutine finalice para continuar con la ejecución en la línea que está inmediatamente después.
 * Nosotros lo que queremos evitar son bloqueos y usar suspensiones en lugar de bloqueos.
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-blocking.html
 */
fun ejemploRunBlocking() {
    log("runBlocking: Inicio")
    runBlocking {
        log("runBlocking")
        delay(1000)
        log("World!")
    }
    log("runBlocking: Fin")
    log("World 2 No se verá hasta que termine la corrutina anterior, porque bloquea el hilo")
}

/**
 * Este constructor crea una coroutine devolviendo un objeto de tipo Job.
 * Debido a que este constructor es una función de extensión de la interfaz CoroutineScope,
 * se puede llamar solamente desde adentro de una coroutine o dentro de una suspend function.
 * Se utiliza para hacer tareas que no requieren la devolución de ningún valor.
 * Como mencioné anteriormente, el constructor launch se puede llamar solamente desde adentro
 * de una coroutine o dentro de una suspend function.
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html
 * job.join: Con está función, puedes bloquear la corrutina asociada con el job hasta que todos los jobs hijos hayan finalizado.
 * job.cancel: Esta función cancelará todos sus jobs hijos asociados.
 */

@OptIn(DelicateCoroutinesApi::class)
fun ejemploLaunch() = runBlocking {
    log("launch: Inicio")
    // No uses GlobalScope tan alegremente!!!! usa tu propio scope
    // Lanzamos tareas en segundo plano en paralelo!!!!
    val job1 = GlobalScope.launch {
        log("launch 1")
        delay(1000)
        log("Fin launch 1")
    }

    val job2 = GlobalScope.launch {
        log("launch2")
        delay(1000)
        log("Fin launch 2")
    }

    log("launch Todos: Fin???")
    // Debes esperarlas!!!, y aunque ahora lo hagaos asñi luego lo cambiaremos!!!
    job1.join()
    job2.join()
    log("launch Todos: Fin")
}

/**
 * Este constructor crea una coroutine devolviendo un objeto de tipo Deferred siendo T el tipo de dato esperado.
 * Por ejemplo Int, String, etc. Debido a que el constructor async es una función de extensión de la interfaz
 * CoroutineScope, se puede llamar solamente desde adentro de una coroutine o dentro de una suspend function.
 * Se utiliza para hacer tareas que requieren la devolución de algún valor. O por ejemplo poder lanzar dos
 * cosas en paralelo.
 * Permite ejecutar varias tareas en segundo plano en paralelo.
 * No es una función de suspensión en sí misma, por lo que cuando ejecutamos async,
 * el proceso en segundo plano se inicia, pero la siguiente línea se ejecuta de inmediato. Salvoq ue lo defina con Lazy
 * Este objeto tiene una nueva función llamada await() que es la que bloquea.
 * Llamaremos a await() solo cuando necesitemos el resultado. Si el resultado aún no esta listo,
 * la corrutina se suspende en ese punto.
 * Si ya tenemos el resultado, simplemente lo devolverá y continuará. De esta manera,
 * puedes ejecutar tantas tareas en segundo plano como necesites.
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html
 */

@OptIn(DelicateCoroutinesApi::class)
// Si ppnemos run Blocking no hace falta ponerlo dentro!!!, no va a acabar hasta que no acaben las corrutinas
fun ejemploAsync() = runBlocking {
    log("async: Inicio")
    // No uses GlobalScope tan alegremente!!!! usa tu propio scope
    // Lanzamos tareas en segundo plano en paralelo
    // Devuelven un valor!!
    val entero = GlobalScope.async {
        log("async 1")
        delay(1000)
        log("Fin async 1")
        1
    }
    val string = GlobalScope.async {
        log("async 2")
        delay(1000)
        log("Fin async 2")
        "Hola"
    }

    // Cuando queramos usar el valor de la corrutina, debemos esperarla
    // y luego usar el valor que nos devuelve
    // No uses run blocking, luego lo quitaremos
    //runBlocking {
    log("async: ${entero.await()}")
    log("async: ${string.await()}")
    //}
    log("async: Fin")
}
