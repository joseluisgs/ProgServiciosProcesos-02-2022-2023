package JamonesSharedFlow

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import mu.KotlinLogging

/**
 * Ideal para el 1 a muchos!!
 * Es decir que el estado de un objeto se comparte con muchos subscriptores
 * SharedFlow, un flujo caliente que emite valores para todos los consumidores que recopilan datos de él.
 * Un SharedFlow es una generalización que admite una amplia configuración de StateFlow.
 * Como ejemplo, puedes usar un SharedFlow para enviar marcas
 * al resto de la app, de modo que todo el contenido se actualice simultáneamente y de manera periódica
 */

const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }

private class Granja(val id: String) {
    val _jamones = MutableSharedFlow<Jamon>(extraBufferCapacity = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val jamones: SharedFlow<Jamon> = _jamones.asSharedFlow()
    suspend fun producirJamones() {
        for (i in 1..MAX_JAMONES) {
            delay(INTER_GRANJA.toLong() * (1..3).random())
            val jamon = Jamon(id = i, idGranja = id)
            logger.debug { "Granja $id produce jamón $jamon" }
            _jamones.emit(jamon)
        }
    }
}

private class Mensajero(val id: String) {
    suspend fun generarLotes(jamonesFlow: SharedFlow<Jamon>) {
        var numLotes = 1
        val jamones = mutableListOf<Jamon>()
        jamonesFlow.buffer(10).distinctUntilChanged().collect { jamon ->
            delay(INTER_MENSA.toLong())
            logger.debug { "Mensajero: $id recibe jamón $jamon" }
            jamones.add(jamon)
            if (jamones.size % TAM_LOTE == 0) {
                logger.debug { "Mensajero: $id envía lote de jamones $jamones" }
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
    val granja = Granja("Granja-1")
    val mensajeros = listOf(Mensajero("Mensajero-1"), Mensajero("Mensajero-2"))

    launch { granja.producirJamones() }

    for (mensajero in mensajeros) {
        launch { mensajero.generarLotes(granja.jamones) }
    }
}