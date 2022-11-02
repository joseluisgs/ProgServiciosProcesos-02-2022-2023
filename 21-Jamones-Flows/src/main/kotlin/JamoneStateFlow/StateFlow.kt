package JamoneStateFlow

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import mu.KotlinLogging

/**
StateFlow es un flujo observable contenedor de estados que emite actualizaciones de estado actuales
y nuevas a sus recopiladores. El valor de estado actual también se puede leer a través de su propiedad value.
Para actualizar el estado y enviarlo al flujo, asigna un nuevo valor a la propiedad value de la clase
MutableStateFlow.
StateFlow es una excelente opción para clases que necesitan mantener un estado observable que muta.
 */
const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }

private class Secadero {
    private val jamones = MutableStateFlow<List<Jamon>>(listOf())

    //val jamonesDisponibles = jamones.asStateFlow()
    val ocupacion
        get() = jamones.value.size


    // Nos permite añadir un jamón al secadero
    suspend fun addJamon(jamon: Jamon) {
        logger.debug { "Secadero recibe jamón $jamon" }
        jamones.value += jamon
    }

    // si queremos devolver los jamones en tamaño de lote
    suspend fun getLote(): List<Jamon> {
        val lote = jamones.value.take(TAM_LOTE)
        jamones.value = jamones.value.drop(TAM_LOTE)
        logger.debug { "Secadero envía jamones para lote: $lote" }
        return lote
    }

    // si queremos devolver los jamones uno a uno
    suspend fun getJamon(): Jamon {
        val jamon = jamones.value.first()
        jamones.value = jamones.value.drop(1)
        logger.debug { "Secadero envía jamón: $jamon" }
        return jamon
    }
}

private class Granja(val id: String, val secadero: Secadero) {
    suspend fun producirJamones() {
        for (i in 1..MAX_JAMONES) {
            delay(JamonesFlow.INTER_GRANJA.toLong() * (1..3).random())
            val jamon = Jamon(id = i, idGranja = id)
            logger.debug { "Granja $id produce jamón $jamon" }
            secadero.addJamon(jamon)
        }
    }
}

private class Mensajero(val id: String) {
    suspend fun generarLotes(secadero: Secadero) {
        var numLotes = 1
        val jamones = mutableListOf<Jamon>()

        while (numLotes <= (MAX_JAMONES * 2 / TAM_LOTE)) {
            delay(JamonesFlow.INTER_MENSA.toLong())
            // Podemos sacar
            if (secadero.ocupacion > 0) {
                jamones.add(secadero.getJamon())
                if (jamones.size % TAM_LOTE == 0) {
                    logger.debug { "Mensajero envía lote de jamones $jamones" }
                    val lote = Lote(id = numLotes, jamones = jamones, idMensajero = id)
                    println("Lote enviado: \n${json.encodeToString(lote)}")
                    jamones.clear()
                    numLotes++
                }
            }
        }
        logger.debug { "Mensajero termina" }
    }
}

fun main(): Unit = runBlocking {
    val secadero = Secadero()
    val granja1 = Granja("Granja-1", secadero)
    val granja2 = Granja("Granja-2", secadero)
    val mensajero = Mensajero("Mensajero-1")

    val granjas = listOf(granja1, granja2)
    granjas.forEach { granja ->
        launch {
            granja.producirJamones()
        }
    }

    // Ojo si tenemos más debemos ajustar la condición de parada del bucle while
    launch {
        mensajero.generarLotes(secadero)
        this.cancel("Mensajero termina")
    }

}
