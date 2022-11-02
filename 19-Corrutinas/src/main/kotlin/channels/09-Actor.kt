package channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import log

/**
 * ¿Recuerdas que el constructor produce retornaba un ReceiveChannel?.
 * El constructor actor es la pareja del constructor produce, es decir,
 * cuando creamos una coroutine utilizando el constructor actor obtenemos un SendChannel de retorno.
 * Es decir, con el constructor actor obtenemos el SendChannel inmediatamente
 * sin la necesidad de crear una coroutine que se encargue de recibir los mensajes que serán
 * enviados a través del canal ya que el propio actor es la coroutine que recibirá los mensajes.
 *
 * Se puede usar para crear estados compartidos. Se uas como Muchos a uno, es deicir
 * muchos productores y un consumidor
 * El modelo de actor es una forma de compartir datos en un entorno de corrutinas múltiples
 * canal una cola dedicada. ES nuestro MONITOR parra compartir datos y secciçon crítica!!
 */

sealed class Message2
object IncCounterMessage2 : Message2()
class GetCounterMessage2(val counterValue: CompletableDeferred<Int>) : Message2()

@OptIn(DelicateCoroutinesApi::class)
fun CoroutineScope.getSendChannel2(): SendChannel<Message2> = actor(newSingleThreadContext("My Thread")) {
    var counter = 0
    // Nos ahorramos crear lounch y el canal
    for (message in channel) {
        when (message) {
            is IncCounterMessage2 -> counter++
            is GetCounterMessage2 -> message.counterValue.complete(counter)
        }
    }
}

fun main() {
    log("Start con Actor")

    val coroutinesAmount = 100_000
    var counter = 0

    log("Initial Value: $counter")

    runBlocking {

        // Obtenemos el canal!!
        val channel = getSendChannel2()

        val coroutines = List(coroutinesAmount) {
            launch(Dispatchers.Default) {
                channel.send(IncCounterMessage2)
            }
        }

        coroutines.forEach {
            it.join()
        }

        val result = CompletableDeferred<Int>()
        channel.send(GetCounterMessage2(result))
        counter = result.await()
        channel.close()
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