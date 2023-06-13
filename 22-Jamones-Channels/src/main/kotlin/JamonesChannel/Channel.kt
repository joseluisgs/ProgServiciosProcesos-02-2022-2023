package JamonesChannel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import mu.KotlinLogging

/**
 * Un Channel se utiliza para establecer una conexión entre un emisor y un receptor.
 * Esta conexión puede ser cerrada o cancelada, y todas las operaciones relacionadas son suspendable.
 * Un Channel es un flujo de datos unidireccional, es decir, un canal solo puede ser usado para enviar o recibir datos.
 * Un Channel puede ser de tipo SendChannel o ReceiveChannel, dependiendo de si se puede enviar o recibir datos.
 * Podemos usarlo para hacer un N..M, N productores y M consumidores
 */

const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }

// Por gusto personal, he decidido crear una clase para el canal de jamones
private object Secadero {
    val channel = Channel<Jamon>()

    val puertaEntrada
        get() = channel as SendChannel<Jamon>
    val puertaSalida
        get() = channel as ReceiveChannel<Jamon>
}

private class Granja(val id: String) {
    // Lo podría pasar por constructor, pero así puedo cambiar de secaderos
    suspend fun producirJamones(secadero: SendChannel<Jamon>) {
        for (i in 1..MAX_JAMONES) {
            delay(INTER_GRANJA.toLong() * (1..3).random())
            val jamon = Jamon(id = i, idGranja = id)
            logger.debug { "Granja $id produce jamón $jamon" }
            secadero.send(jamon)
        }
        logger.debug { "Granja: $id ha terminado poner" }
    }
}

private class Mensajero(val id: String) {
    // Lo podría pasar por constructor, pero así puedo cambiar de secadero
    suspend fun generarLotes(secadero: ReceiveChannel<Jamon>) {
        var numLotes = 1
        val jamones = mutableListOf<Jamon>()
        for (jamon in secadero) {
            delay(INTER_MENSA.toLong())
            logger.debug { "Mensajero recibe jamón $jamon" }
            jamones.add(jamon)
            if (jamones.size % TAM_LOTE == 0) {
                logger.debug { "Mensajero envía lote de jamones $jamones" }
                val lote = Lote(id = numLotes, jamones = jamones, idMensajero = id)
                println("Lote enviado: \n${json.encodeToString(lote)}")
                jamones.clear()
                numLotes++
            }
        }
        logger.debug { "Mensajero: $id ha terminado coger" }
    }
}

fun main() = runBlocking<Unit> {
    // Podría ser un Singleton o crear el channel directamente

    // con este tipo de canal podemos crear tantos productores y consumidores como queramos
    // si son muchos productores hacemos un find-in y si son muchos consumidores un find-out

    val granjas = listOf(Granja("Granja-1"), Granja("Granja-2"))

    // Podríamos tener varios
    /* val mensajeros = listOf(
         Mensajero("Mensajero-1"),
         Mensajero("Mensajero-2")
     )
 */
    val mensajero = Mensajero("Mensajero-1")

    // lanzamos en paralelo
    granjas.forEach { granja ->
        launch {
            granja.producirJamones(Secadero.puertaEntrada)
            logger.debug { "Granja: ${granja.id} fin" }
        }
    }

    // podemos tener uno o varios
    launch {
        mensajero.generarLotes(Secadero.puertaSalida)
        logger.debug { "Mensajero: ${mensajero.id} fin" }
    }

    /* mensajeros.forEach { mensajero ->
         launch {
             mensajero.generarLotes(Secadero.puertaSalida)
             logger.debug { "Mensajero: ${mensajero.id} fin" }
         }
     }*/

}