package JamonesActor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

/**
 * actor es un costructor que crea un canal de entrada: SendChannel
 * Es ideal para hacer un M a 1, es muchos a uno.
 * Es decir que muchos objetos se envian a un solo subscriptor
 */

const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }


private class Granja(val id: String) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    suspend fun producirJamones(secadero: SendChannel<Jamon>) {
        for (i in 1..JamonesProducer.MAX_JAMONES) {
            delay(INTER_GRANJA.toLong() * (1..3).random())
            val jamon = Jamon(id = i, idGranja = id)
            JamonesProducer.logger.debug { "Granja $id produce jamón $jamon" }
            secadero.send(jamon)
        }
        JamonesProducer.logger.debug { "Granja: $id ha terminado de poner" }
    }

    fun release() {
        this.job.cancel()
    }
}

private class Mensajero(val id: String) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    // Lo podría pasar por constructor, pero así puedo cambiar de secadero
    @OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
    fun generarLotes() = actor<Jamon> {
        var numLotes = 1
        val jamones = mutableListOf<Jamon>()
        for (jamon in channel) {
            delay(JamonesProducer.INTER_MENSA.toLong())
            JamonesProducer.logger.debug { "Mensajero recibe jamón $jamon" }
            jamones.add(jamon)
            if (jamones.size % JamonesProducer.TAM_LOTE == 0) {
                JamonesProducer.logger.debug { "Mensajero envía lote de jamones $jamones" }
                val lote = Lote(id = numLotes, jamones = jamones, idMensajero = id)
                println("Lote enviado: \n${JamonesProducer.json.encodeToString(lote)}")
                jamones.clear()
                numLotes++
            }
        }
        JamonesProducer.logger.debug { "Mensajero: $id ha terminado de coger" }
    }

    fun release() {
        this.job.cancel()
    }
}


fun main() = runBlocking<Unit> {
    val granjas = listOf(
        Granja("Granja1"),
        Granja("Granja2")
    )

    // Mensajero
    val mensajero = Mensajero("Mensajero1")

    // Secadero
    val secadero = mensajero.generarLotes()

    // podrmiamos tener muchas granjas
    granjas.forEach { granja ->
        launch {
            granja.producirJamones(secadero)
            granja.release()
            logger.debug { "Granja ${granja.id} fin" }
        }
    }

}

