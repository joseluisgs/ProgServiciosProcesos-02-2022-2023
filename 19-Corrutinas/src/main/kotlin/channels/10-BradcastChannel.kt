package channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import log

/**
 * BroadcastChannel
 * Un canal que permite tener múltiples consumidores que se suscriben a él.
 *
 */
fun main() {
    ej01()
    ej02()
    ej03()
}

fun ej01() {
    log("Start")

    runBlocking {
        log("RunBlocking start")

        val bc: BroadcastChannel<Int> = BroadcastChannel(1)

        val job1 = launch(Dispatchers.Default) {
            (1..5).forEach { number ->
                log("Sending $number...")
                bc.send(number)
                delay(500)
            }
            bc.close()
        }

        delay(1250)

        val job2 = launch(Dispatchers.Default) {
            bc.consumeEach { number ->
                log("Receiver One: $number")
            }
        }

        val job3 = launch(Dispatchers.Default) {
            bc.consumeEach { number ->
                log("Receiver Two: $number")
            }
        }

        joinAll(job1, job2, job3)
        log("RunBlocking end")

    }

    log("End")
}

fun ej02() {
    log("Start")

    runBlocking {
        log("RunBlocking start")

        val bc: ConflatedBroadcastChannel<Int> = ConflatedBroadcastChannel()

        val job1 = launch(Dispatchers.Default) {
            (1..5).forEach { number ->
                log("Sending $number...")
                bc.send(number)
                delay(500)
            }
            bc.close()
        }

        delay(1250)

        val job2 = launch(Dispatchers.Default) {
            bc.consumeEach { number ->
                log("Receiver One: $number")
            }
        }

        val job3 = launch(Dispatchers.Default) {
            bc.consumeEach { number ->
                log("Receiver Two: $number")
            }
        }

        joinAll(job1, job2, job3)
        log("RunBlocking end")

    }

    log("End")
}

fun ej03() {
    log("Start")

    runBlocking {
        log("RunBlocking start")

        val bc: ConflatedBroadcastChannel<Int> = ConflatedBroadcastChannel()

        launch(Dispatchers.Default) {
            val receiveChannel = bc.openSubscription()
            for (number in receiveChannel) {
                log("Receiver: $number")
                delay(1000)
            }
        }

        val job = launch(Dispatchers.Default) {
            (1..10).forEach { number ->
                log("Sending $number...")
                bc.send(number)
                delay(300)
            }
            bc.close()
        }

        job.join()

        log("RunBlocking end")
    }

    log("End")
}