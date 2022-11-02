package JamonesSemaphore

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Jamon
import models.Lote
import mu.KotlinLogging


val logger = KotlinLogging.logger {}
val json = Json { prettyPrint = true }

const val MAX_JAMONES = 30
const val INTER_GRANJA = 500
const val TAM_LOTE = 3
const val INTER_MENSA = 1000

fun main() = runBlocking<Unit> {
    // Productor - Consumidor
    val queue = mutableListOf<Jamon>() // necesitas BlockingLinkedList??

    val semaforo = Semaphore(1) // Implementa un semáforo

    for (granja in 1..2) {
        launch {
            for (i in 1..MAX_JAMONES) {
                delay(INTER_GRANJA.toLong() * (1..3).random())
                semaforo.withPermit {
                    val jamon = Jamon(id = i, idGranja = "Granja-$granja")
                    queue.add(jamon) // Añade el jamón a la cola
                    logger.debug { "Granja $granja produce jamón $jamon" }
                }
            }
        }
    }

    launch {
        var numLotes = 1
        val jamones = mutableListOf<Jamon>()
        for (j in 1..MAX_JAMONES * 2) {
            delay(INTER_MENSA.toLong())
            semaforo.withPermit {
                val jamon = queue.removeFirst() // Elimina el jamón de la cola
                logger.debug { "Mensajero recibe jamón $jamon" }
                jamones.add(jamon)
            }
            if (jamones.size % TAM_LOTE == 0) {
                logger.debug { "Mensajero envía lote de jamones $jamones" }
                val lote = Lote(id = numLotes, jamones = jamones, idMensajero = "Mensajero-1")
                println("Lote enviado: \n${json.encodeToString(lote)}")
                jamones.clear()
                numLotes++
            }
        }
    }

}

/*
Si tienes experiencia sincronizando accesos a variables sabes que cada vez que estableces puntos de control synchronized
o Semaphore en nuestro caso, estás creando cuellos de botella. Para entender ésto mejor, te lo ilustro con el siguiente
ejemplo: Imagina una autopista de 6 carriles con un punto de peaje de un solo paso en el kilómetro 10.
Inevitablemente en un momento de mucho tránsito se generará un embotellamiento.
Ésto es precisamente lo que pasa cuando estableces bloques donde solamente un hilo a la vez puede estar en ejecución.
 */