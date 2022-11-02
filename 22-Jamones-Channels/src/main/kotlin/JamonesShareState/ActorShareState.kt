package JamonesShareState

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
 * Su principal uso es crear un estado compartido a través de mensajes en el canal.
 */

const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }

// Creamos la clases Sealed que nos permitiran acotar los mensajes que se pueden enviar y recivir
sealed class SecaderoMsg
class InsertarJamon(val jamon: Jamon) : SecaderoMsg()
class ObtenerJamon(val jamon: CompletableDeferred<Jamon?>) : SecaderoMsg()

private object Secadero : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val jamones = mutableListOf<Jamon>()

    @OptIn(ObsoleteCoroutinesApi::class)
    fun peticiones() = actor<SecaderoMsg> {
        var numLotes = 1
        for (msg in channel) {
            when (msg) {
                is InsertarJamon -> {
                    logger.debug { "Secadero recibe jamón ${msg.jamon}" }
                    jamones.add(msg.jamon)
                }

                is ObtenerJamon -> {
                    if (jamones.size > 0) {
                        val jamon = jamones.removeAt(0)
                        logger.debug { "Secadero entrega jamón $jamon" }
                        // Devolvemos el primero
                        msg.jamon.complete(jamon)
                    } else {
                        logger.debug { "Secadero no tiene jamones" }
                        msg.jamon.complete(null)
                    }
                }
            }
        }
    }
}

private class Granja(val id: String) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    suspend fun producirJamones(secadero: SendChannel<SecaderoMsg>) {
        for (i in 1..JamonesProducer.MAX_JAMONES) {
            delay(INTER_GRANJA.toLong() * (1..3).random())
            val jamon = Jamon(id = i, idGranja = id)
            JamonesProducer.logger.debug { "Granja $id produce jamón $jamon" }
            secadero.send(InsertarJamon(jamon))
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
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun generarLotes(secadero: SendChannel<SecaderoMsg>) {
        var numLotes = 1
        var jamonTotales = 0
        val jamones = mutableListOf<Jamon>()
        // Si no ha terminado, vamos a por un jamón
        do {
            delay(INTER_MENSA.toLong())
            // Consumimos
            val jamonAsync = CompletableDeferred<Jamon?>()
            secadero.send(ObtenerJamon(jamonAsync))
            val jamon = jamonAsync.await()
            jamon?.let {
                // println("Mensajero $id recibe jamón $jamon")
                jamones.add(jamon)
                jamonTotales++
                // println("Mensajero tiene $i jamones")
                if (jamones.size % TAM_LOTE == 0) {
                    val lote = Lote(id = numLotes, jamones = jamones, idMensajero = id)
                    logger.debug { "Mensajero $id entrega lote $lote" }
                    println("Lote enviado: \n${json.encodeToString(lote)}")
                    numLotes++
                    jamones.clear()
                }
            }

        } while (jamonTotales < MAX_JAMONES)
        logger.debug { "Mensajero $id ha terminado de coger" }
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

    // Podemos tener 1 o más mensajeros
    val mensajeros = listOf(
        Mensajero("Mensajero-1"),
        Mensajero("Mensajero-2")
    )

    // podrmiamos tener muchas granjas
    granjas.forEach { granja ->
        launch {
            granja.producirJamones(Secadero.peticiones())
            granja.release()
            logger.debug { "Granja ${granja.id} fin" }
        }
    }

    // Y muchos mensajeros, porque al final el stado es unic, por eso es M->1, aunque esos
    // M solo mandan mensajes de distinto tipo

    mensajeros.forEach { mensajero ->
        launch {
            mensajero.generarLotes(Secadero.peticiones())
            mensajero.release()
            logger.debug { "Mensajero ${mensajero.id} fin" }
        }
    }

}

