package corrutinas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import log
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html
 *
 * Semáforos: Dentro del mundo de la concurrencia contamos con una estructura de administración
 * de subprocesos llamada semáforo. Un semáforo es básicamente una estructura de control de
 * subprocesos por medio de la adjudicación de permisos.
 * Un subproceso puede tomar el papel de “adquisidor” o el papel de “liberador”.
 * Cuando un subproceso desea acceder a un recurso compartido, toma el papel de adquisidor
 * e intenta adquirir un permiso antes de hacerlo.
 * Cuando un subproceso desocupa el recurso, toma el papel de liberador y libera el permiso
 * que se le había concedido.
 * - En Kotlin podemos usar la clase Semaphore para implementar semáforos y proteger la
 * sección crítica con los métodos acquire y release.
 * Opcionalmente podemos usar withPermit para simplificar la sintaxis de la llamada a acquire y release.
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.sync/-semaphore/
 *
 * Monitores: Los monitores son estructuras de control de subprocesos basados en la sincronización.
 * Funcionan de manera muy similar a un semáforo.
 * Básicamente un monitor es una estructura de encapsulamiento que oculta sus variables globales y
 * lógica de negocio y que ofrece acceso al mundo exterior mediante métodos de servicio.
 * Un monitor es por naturaleza un componente de sincronización por exclusión mutua,
 * esto quiere decir que solamente un subproceso a la vez puede estar en ejecución
 * dentro del monitor haciendo uso de alguno de sus métodos de servicio.
 * Un monitor cuenta con un cerrojo que garantizará que solamente un subproceso a la vez
 * se encuentra en ejecución dentro de las zonas críticas de su código.
 *
 * - Al usar un ReentrantLock puedes crear tantas Conditions como desees ganando
 * así la posibilidad de interrumpir subprocesos separadamente a partir de un mismo cerrojo.
 * Ésto te permite clasificar mejor los subprocesos pudiendo generar subgrupos sin necesidad de
 * crear varios cerrojos para ello. Dos cosas a tener en cuenta para implementar monitores:
 * ReentrantLock que resulta mejor que un objeto de tipo Object (con Synchronized, wait y notifyAll)
 * dado que está especialmente adaptado para lidiar con concurrencia y sincronización Condition.
 * Al usar un ReentrantLock puedes crear tantas Conditions como desees ganando así la posibilidad
 * de interrumpir subprocesos separadamente a partir de un mismo cerrojo.
 * Ésto te permite clasificar mejor los subprocesos pudiendo generar subgrupos sin necesidad de
 * crear varios cerrojos para ello.
 *
 * El objeto de tipo Mutex Es el equivalente al ReentrantLock.
 * La instrucción mutex.withLock {...} es la forma abreviada de
 * mutex.lock();
 * try { ... }
 * finally { mutex.unlock() }.
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.sync/-mutex/
 */

/*
Estados compartidos, secciones críticas y sección de bloqueo
Empecemos estableciendo un problema sencillo. Vamos a crear un ejemplo en el que se incrementa una variable en 1 desde
cada coroutine creada. Para que se pueda apreciar una inconsistencia vamos a crear 100 mil coroutines, el valor final
 de la variable inicializada en 0 debería ser 100 mil. Veamos:
 */

fun main() = runBlocking<Unit> {
    //nada()
    //semaforo()
    //monitor()
    // atomicidad()
    // mutex()
    //sinDispacher()
    // propioHilo()
}

/*
Si ejecutas el ejemplo anterior te darás cuenta que la variable finaliza con una cantidad menor a la cantidad de coroutines
creadas. La razón por la que se dan inconsistencias se debe a un problema conocido como “condición de carrera” — ‘race condition’
en Inglés — y es un problema a resolver que se da ineludiblemente cuando se trabaja de manera concurrente sobre una variable
u objeto mutable compartido. Básicamente muchos subprocesos acceden a la misma variable en un momento determinado,
 consultan su valor y lo modifican basado en esta consulta. Debido a que hay varios subprocesos leyendo y escribiendo
 sobre una misma variable a la vez, entre la acción de consulta y la acción de modificación de cada subproceso, pueden
 haber otras modificaciones intermedias hechas por los otros subprocesos.
 */

fun nada() {
    log("Start Ejemplo 1")

    val coroutinesAmount = 100_000
    var counter = 0

    log("Initial Value: $counter")

    runBlocking {
        val coroutines = List(coroutinesAmount) {
            launch(Dispatchers.Default) {
                counter++
            }
        }

        coroutines.forEach {
            it.join()
        }
    }

    log("Final Value: $counter")
    log("---------------")

    if (counter == coroutinesAmount) {
        log("Result: SUCCESS")
    } else {
        log("Result: FAIL")
    }
    log("---------------")

    log("End")
}

/*
Para resolver este problema, vamos a utilizar una sección crítica. Protegida por un semáforo binario
Podemos usar acquire() para solicitar un permiso de acceso y release() para liberarlo.
O usar withPermit() para solicitar un permiso de acceso y liberarlo automáticamente al finalizar la función.
 */
fun semaforo() {
    log("Start Semaforos")

    val coroutinesAmount = 100_000
    var counter = 0

    log("Initial Value: $counter")

    runBlocking {
        val semaphore = Semaphore(1) // Binarios o Mutex

        val coroutines = List(coroutinesAmount) {
            launch(Dispatchers.Default) {
                // Protegemos la SC
                semaphore.withPermit {
                    counter++
                }
            }
        }

        coroutines.forEach {
            it.join()
        }
    }

    log("Final Value: $counter")
    log("---------------")
    if (counter == coroutinesAmount) {
        log("Result: SUCCESS")
    } else {
        log("Result: FAIL")
    }
    log("---------------")

    log("End")
}

/**
 * Monitor es una clase que implementa una sección crítica. Accedemos y la modificamos a través de los métodos de la clase.
 * En este ejemplo: Si el id y el valor del contador no coinciden, el subproceso deberá ser interrumpido temporalmente hasta que sea notificado
 * y así reanudar su ejecución para volverlo a intentar.
 * ReentrantLock que resulta mejor que un objeto de tipo Object dado que está especialmente adaptado para lidiar con
 * concurrencia y sincronización
 * Condition. Al usar un ReentrantLock puedes crear tantas Conditions como desees ganando así la posibilidad de
 * interrumpir subprocesos separadamente a partir de un mismo cerrojo. Ésto te permite clasificar mejor los subprocesos
 * pudiendo generar subgrupos sin necesidad de crear varios cerrojos para ello.
 */
class Monitor {
    // este no e sun objeto de Kotlin, es propio de Java!!
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    var counter = 0
        private set

    fun performAction(id: Int) {
        // Va a esperar hasta que no sea su id!!
        lock.withLock {
            while (id != counter) {
                condition.await()
            }

            this.counter++
            condition.signalAll()
        }
    }
}

fun monitor() {
    log("Start Monitor")

    val coroutinesAmount = 100_000

    val monitor = Monitor()

    log("Initial Value: ${monitor.counter}")

    runBlocking {

        val coroutines = List(coroutinesAmount) {
            launch(Dispatchers.Default) {
                monitor.performAction(it)
            }
        }

        coroutines.forEach {
            it.join()
        }
    }

    log("Final Value: ${monitor.counter}")
    log("---------------")
    if (monitor.counter == coroutinesAmount) {
        log("Result: SUCCESS")
    } else {
        log("Result: FAIL")
    }
    log("---------------")

    log("End")
}

/**
 * Usamos variables atómicas para asegurarnos que que podemos leer y escribir concurrentemente
 * Tenemos AtomicInteger, Long, Reference
 *  BlockingQueue y ConcurrentHashMap
 *  un Collection común en un Collection sincronizado por medio de un “envoltorio” llamando a la función
 *  Collections.synchronizedCollection o alguna de sus funciones más específicas como Collections.synchronizedSet,
 *  Collections.synchronizedList, Collections.synchronizedMap, etc. La diferencia entre los Collections del paquete
 *  java.util.concurrent y los Collections sincronizados radica en el desempeño. Los Collections del paquete java.util.concurrent
 *  siempre serán más eficientes o más rápidos debido a su naturaleza de soportar el acceso desde varios subprocesos dividiendo
 *  sus datos en segmentos, mientras que los Collections sincronizados se valen del uso de cerrojos limitando el acceso
 *  a solamente un subproceso a la vez.
 */
fun atomicidad() {
    log("Start Atomicidad")

    val coroutinesAmount = 100_000
    val counter = AtomicInteger(0)

    log("Initial Value: $counter")

    runBlocking {
        val coroutines = List(coroutinesAmount) {
            launch(Dispatchers.Default) {
                counter.incrementAndGet()
            }
        }

        coroutines.forEach {
            it.join()
        }
    }

    log("Final Value: ${counter.get()}")
    log("---------------")
    if (counter.get() == coroutinesAmount) {
        log("Result: SUCCESS")
    } else {
        log("Result: FAIL")
    }
    log("---------------")

    log("End")
}

// Mutex es propoa de Kotlin
fun mutex() {
    log("Start Mutex")

    val coroutinesAmount = 100_000
    var counter = 0

    log("Initial Value: $counter")

    runBlocking {
        val mutex = Mutex() // Es el reentratLock de Kotlin!!

        val coroutines = List(coroutinesAmount) {
            launch(Dispatchers.Default) {
                mutex.withLock {
                    counter++
                }
            }
        }

        coroutines.forEach {
            it.join()
        }
    }

    log("Final Value: ${counter}")
    log("---------------")
    if (counter == coroutinesAmount) {
        log("Result: SUCCESS")
    } else {
        log("Result: FAIL")
    }
    log("---------------")

    log("End")
}

/**
 * La única diferencia entre el primer y estos ejercicios y otro es el hilo encargado de realizar el incremento de la variable.
 * En el código inicial utilizamos el pool de hilos correspondiente a Dispatchers.Default al crear cada coroutine,
 * mientras que en el código modificado no especificamos ningún Dispatcher asignándose así al hilo principal.
 * El “truco” en sí no es precisamente usar el hilo principal, sino que un único hilo, no importa cual,
 * pero solamente ese hilo sea el que modifica la variable. A esta técnica se le conoce como “confinamiento de hilo” —
 * thread confinement” en Inglés — .
 * Puedes usar tu propio hilo dedicado creándolo con una llamada a la función newSingleThreadContext como puedes ver en el otro hilo
 */
fun sinDispacher() {
    log("Start Sin Dispacher")

    val coroutinesAmount = 100_000
    var counter = 0

    log("Initial Value: $counter")

    runBlocking {
        val coroutines = List(coroutinesAmount) {
            launch {
                counter++
            }
        }

        coroutines.forEach {
            it.join()
        }
    }

    log("Final Value: $counter")
    log("---------------")
    if (counter == coroutinesAmount) {
        log("Result: SUCCESS")
    } else {
        log("Result: FAIL")
    }
    log("---------------")

    log("End")
}

fun propioHilo() {
    log("Start Propio Hilo")

    val coroutinesAmount = 100_000
    var counter = 0

    log("Initial Value: $counter")

    runBlocking {
        val dedicatedThread = newSingleThreadContext("My Thread")

        val coroutines = List(coroutinesAmount) {
            launch(dedicatedThread) {
                counter++
            }
        }

        coroutines.forEach {
            it.join()
        }
    }

    log("Final Value: $counter")
    log("---------------")
    if (counter == coroutinesAmount) {
        log("Result: SUCCESS")
    } else {
        log("Result: FAIL")
    }
    log("---------------")

    log("End")
}