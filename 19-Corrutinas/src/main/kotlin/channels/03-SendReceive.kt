package channels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import log

/**
 * Interfaces SendChannel y ReceiveChannel
Cuando programas de una manera bien estructura aplicando los principios
de abstracción y encapsulamiento, limitas las acciones que se pueden realizar desde
el exterior de un objeto, evitando comprometer los datos más de lo estrictamente necesario.
Teniendo en cuenta ésto, puedes enviar y recibir Channels sin exponer toda su implementación
simplemente pasando como parámetro o retornando un SendChannel o un ReceiveChannel
según sea el caso. Si queremos enviar un mensaje a un Channel,
debemos usar el método send de la interfaz SendChannel por lo que no podremos recibir.
Si queremos recibir un mensaje de un Channel debemos usar el método receive de la interfaz
ReceiveChannel por lo que no podremos enviar. De esta manera controlamos el uso que se le da.
 */

// Clase que recibe un canal para enviar las cosas, será el productor
class Sender {
    companion object {
        const val MESSAGES_AMOUNT = 1000
    }

    // Envía una cantidad de mensajes, por eso necesita el send!!!
    suspend fun processAction(channel: SendChannel<Int>) = withContext(Dispatchers.Default) {
        // Repite 1000 veces el envío de un mensaje aleatorio
        repeat(MESSAGES_AMOUNT) {
            channel.send((1..100).random())
        }
    }
}

// Receptor, será el consumidor
class Receiver {
    var messagesAmount = 0
        private set

    // Recibe de un canal, por eso necesita el receive!!!
    suspend fun processAction(channel: ReceiveChannel<Int>) = withContext(Dispatchers.Default) {
        for (i in channel) {
            messagesAmount++
        }
    }
}

fun main() {
    log("Start")

    val channel = Channel<Int>() // Nuestro canal
    val sendersAmount = 100 // 100 productores * 1000 mensajes = 100.000 mensajes
    val receiversAmount = 5 // 5 receptores

    val amounts = IntArray(receiversAmount) // Array para almacenar los resultados de los receptores

    runBlocking {
        launch {
            // Lanzamos los productores
            val senders = List(sendersAmount) {
                val sender = Sender()
                // Realiza el envío de mensajes por el canal!!
                launch { sender.processAction(channel) }
            }

            senders.forEach { it.join() }
            channel.close()
            log("Senders finished!")
        }

        // Lanzamos consumidores
        launch {
            val receivers = List(receiversAmount) {
                val receiver = Receiver()
                // Realiza la recepción de mensajes
                launch {
                    receiver.processAction(channel)
                    amounts[it] = receiver.messagesAmount
                }
            }

            receivers.forEach { it.join() }
            log("Receivers finished!")
        }

    }

    var total = 0
    log("----- AMOUNTS ------")
    amounts.forEachIndexed { index, amount ->
        total += amount
        log("Index #$index: $amount")
    }
    log("--------------------")
    log("TOTAL = $total")
    log("--------------------")

    if (total == sendersAmount * Sender.MESSAGES_AMOUNT) {
        log("Final State: SUCCESS")
    } else {
        log("Final State: FAIL")
    }

    log("--------------------")
    log("End")
}