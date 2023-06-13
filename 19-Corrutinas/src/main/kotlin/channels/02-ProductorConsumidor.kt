package channels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import log

// Productor Consuimidor usando Canales
fun main() {
    log("Start")

    val mutex = Mutex()

    val channel = Channel<Int>() // Canal de enteros de un solo elemento de tamaño
    // si queremos más tamaño debemos val channel = Channel<Int>(5) // 5

    var produced = 0
    var consumed = 0

    val consumers = IntArray(5)

    runBlocking {
        launch(Dispatchers.Default) {
            val producers = List(100_000) {
                launch {
                    val number = (1..100).random()
                    // Usamos un paso de mensajes. Es decir,
                    // se suspende hasta que se ejecuta recieve y viceversa.
                    channel.send(number)
                    mutex.withLock {
                        produced++ // Incrementamos el contador de producidos
                    }
                }
            }

            producers.forEach { it.join() }
            channel.close()
            log("Producers finished!")
        }

        launch(Dispatchers.Default) {
            val consumers = List(consumers.size) {
                launch {
                    // No hace falta la espera activa, porque hasta que no haya un dato no podemos "sacarlo"
                    // dentro del for estamos haciendo el recieve implicito
                    // internamente tambien tenemos ya corrutinas con la parte d eproductor y consumidor hechos
                    // https://kotlinlang.org/docs/channels.html#pipelines
                    for (i in channel) {
                        mutex.withLock {
                            consumed++
                            consumers[it]++
                        }
                    }
                }
            }

            consumers.forEach { it.join() }
            log("Consumers finished!")
        }
    }

    log("Produced: $produced")
    log("Consumed: $consumed")

    var total = 0
    log("----- AMOUNTS ------")
    consumers.forEachIndexed { index, amount ->
        total += amount
        log("Index #$index: $amount")
    }
    log("--------------------")
    log("TOTAL = $total")
    log("--------------------")

    log("End")
}