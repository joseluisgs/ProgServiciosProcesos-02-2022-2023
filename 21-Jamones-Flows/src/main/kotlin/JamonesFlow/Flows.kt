package JamonesFlow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import mu.KotlinLogging

/**
 * Un flujo es conceptualmente una transmisión de datos que se puede computar de forma asíncrona.
 * Los valores emitidos deben ser del mismo tipo. Por ejemplo, un Flow<Int> es un flujo que emite valores enteros.
 * Un flujo es muy similar a un Iterator que produce una secuencia de valores,
 * pero usa funciones de suspensión para producir y consumir valores de forma asíncrona.
 * Para cada nuevo consumidor, un flujo produce una nueva secuencia de valores.
 * Esto significa que, por ejemplo, el flujo puede enviar de forma segura una solicitud de red
 * para producir el siguiente valor sin bloquear el subproceso principal.
 * Hay tres entidades involucradas en transmisiones de datos:
 * Un productor produce datos que se agregan al flujo. Gracias a las corrutinas,
 * los flujos también pueden producir datos de forma asíncrona.
 * Los intermediarios (opcional) pueden modificar cada valor emitido en el flujo, o bien el flujo mismo.
 * Un consumidor consume los valores del flujo.
 */

const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }

private class Granja(val id: String) {
    fun producirJamones() = flow {
        for (i in 1..MAX_JAMONES) {
            delay(INTER_GRANJA.toLong() * (1..3).random())
            val jamon = Jamon(id = i, idGranja = id)
            logger.debug { "Granja $id produce jamón $jamon" }
            emit(jamon)
        }
    }.flowOn(Dispatchers.IO) // Esto no es obligatorio
}

private class Mensajero(val id: String) {
    suspend fun generarLotes(jamonesFlow: Flow<Jamon>) {
        var numLotes = 1
        val jamones = mutableListOf<Jamon>()
        jamonesFlow.buffer(10).distinctUntilChanged().collect { jamon ->
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
        logger.debug { "Mensajero termina" }
    }
}

@OptIn(FlowPreview::class)
fun main() = runBlocking<Unit> {
    val granjas = listOf(Granja("Granja-1"), Granja("Granja-2"))
    val mensajero = Mensajero("Mensajero-1")

    val jamonesFlow = granjas.asFlow()
        .flatMapMerge(granjas.size) { granja -> granja.producirJamones() }


//    val granja1Flow = granjas[0].producirJamones()
//    val granja2Flow = granjas[1].producirJamones()
//    val jamonesFlow = merge(granja1Flow, granja2Flow)

//    val jamonesFlow = flowOf(granjas[0].producirJamones(), granjas[1].producirJamones()).
//        flattenMerge(granjas.size)

    launch {
        mensajero.generarLotes(jamonesFlow)
        this.cancel("Mensajero termina")

    }
}