import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedQueue

// Productor consumidor, pero...
// Un productor procuce un elemento, tendremos 100_000 se producen 100_000
// solo 5 consumidores
// Vamos a contar cuantos cuantos items ha consumido un consumidor determinado

fun main() {
    log("Start")
// Productor - Consumidor
    val mutex = Mutex() // Implementa un cerrojo un mutex
    val queue = ConcurrentLinkedQueue<Int>() // Lista protegida para ser accedida por varios hilos
    var closed = false

    var produced = 0
    var consumed = 0

    val amounts = IntArray(5) // 5 consumidores

    // Ejecutamos de forma de bloque, es decir, el hilo principal espera a que todos los hilos terminen
    runBlocking {
        // Lanzamos los productores concurrentemente con los consumidores, en este caso son 100_000
        launch(Dispatchers.Default) {

            val producers = List(100_000) {
                launch {
                    // Generamos un número aleatorio
                    val number = (1..100).random()
                    if (queue.offer(number)) {
                        // De forma excluyente, indicamos que hemos producido
                        mutex.withLock {
                            produced++
                        }
                    }
                }
            }
            // Esperamos que todos los productores terminen
            producers.forEach { it.join() }
            closed = true
            log("Producers finished!")
        }

        // Concurrentemente, lanzamos los consumidores
        launch(Dispatchers.Default) {
            // Lanzamos 5, que es el array que tenemos.
            // En cada posicion va a ir el número de elementos consumidos
            val consumers = List(amounts.size) {
                launch {
                    // Simulamos el monitor. Consumimos mientras nos e haya terminado y no esté vaciio, si no espera activa.
                    while (!closed || !queue.isEmpty()) {
                        if (!queue.isEmpty()) {
                            // sacamos
                            val number = queue.poll()
                            if (number != null) {
                                // De forma exclusiva indicamos que hemos consumido
                                mutex.withLock {
                                    consumed++
                                    // Indica cuanto ha consumido cada uno
                                    amounts[it]++
                                }
                            }
                        }
                    }
                }
            }
            // Esperamos que todos los consumidores terminen
            consumers.forEach { it.join() }
            log("Consumers finished!")
        }
    }
    // Mostramos los resultados
    log("Queue size: ${queue.size}") // Cola que queda
    log("Produced: $produced")
    log("Consumed: $consumed")

    var total = 0
    log("----- AMOUNTS ------")
    amounts.forEachIndexed { index, amount ->
        total += amount
        log("Index #$index: $amount")
    }
    // Suma
    log("--------------------")
    log("TOTAL = $total")
    log("--------------------")

    log("End")
}

/*
Si tienes experiencia sincronizando accesos a variables sabes que cada vez que estableces puntos de control synchronized
o Mutex.withLock en nuestro caso, estás creando cuellos de botella. Para entender ésto mejor, te lo ilustro con el siguiente
ejemplo: Imagina una autopista de 6 carriles con un punto de peaje de un solo paso en el kilómetro 10.
Inevitablemente en un momento de mucho tránsito se generará un embotellamiento.
Ésto es precisamente lo que pasa cuando estableces bloques donde solamente un hilo a la vez puede estar en ejecución.
 */