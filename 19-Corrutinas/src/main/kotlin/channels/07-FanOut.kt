package channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Fan-out
 * Muchos consumidores en el mismo canal
 */

fun main() = runBlocking<Unit> {
    // un productor
    val producer = produceNumbers2()
    // 5 Consumidores
    repeat(5) { launchProcessor(it, producer) }
    delay(950)
    producer.cancel() // cancel producer coroutine and thus kill them all
}

// produceNumbers produce enteros y devuelve un channel de recepcion
fun CoroutineScope.produceNumbers2() = produce<Int> {
    var x = 1 // start from 1
    while (true) {
        send(x++) // produce next
        delay(100) // wait 0.1s
    }
}

// Cada consumidor recibe los elementos del canal
fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
    for (msg in channel) {
        println("Processor #$id received $msg")
    }
}