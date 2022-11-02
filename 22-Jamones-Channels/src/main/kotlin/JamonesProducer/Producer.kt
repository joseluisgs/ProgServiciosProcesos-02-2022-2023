package JamonesProducer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

/**
 * producer es un constructor de canales que devuelve un canal de salida: ReceiveChannel.
 * Es ideal para hacer un 1 a muchos (1-M), es decir que un objeto se comparte con muchos subscriptores
 */

const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }

// Vamos a hacerlo completo con el ejemplo, no es necesario todo esto
// Para crearme el scope, pero lo hago para que se vea completo
private class Granja(val id: String) : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    @OptIn(ExperimentalCoroutinesApi::class)
    fun producirJamones() = produce {
        for (i in 1..MAX_JAMONES) {
            delay(INTER_GRANJA.toLong() * (1..3).random())
            val jamon = Jamon(id = i, idGranja = id)
            logger.debug { "Granja $id produce jamón $jamon" }
            send(jamon)
        }
        logger.debug { "Granja: $id ha terminado de poner" }
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
        logger.debug { "Mensajero: $id ha terminado de coger" }
    }

    fun release() {
        this.job.cancel()
    }
}

fun main() = runBlocking<Unit> {
    // Podría ser un Singleton o crear el channel directamente

    // con este tipo de canal podemos crear tantos productores y consumidores como queramos
    // si son muchos productores hacemos un find-in y si son muchos consumidores un find-out

    val granja = Granja("Granja-1")
    val mensajeros = listOf(Mensajero("Mensajero-1"), Mensajero("Mensajero-2"))

    // Obtenemos el canal de la granja
    val secadero = granja.producirJamones()

    mensajeros.forEach { mensajero ->
        launch {
            mensajero.generarLotes(secadero)
            //this.cancel("Mensajero ${mensajero.id} termina")
            mensajero.release()
            logger.debug { "Mensajero ${mensajero.id} fin" }
        }
    }
}