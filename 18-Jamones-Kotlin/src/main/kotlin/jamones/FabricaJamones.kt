package jamones

import es.joseluisgs.dam.jamones.monitor.SecaderoLock
import jamones.models.Granja
import jamones.models.Mensajero
import jamones.monitor.SecaderoSync
import java.util.concurrent.Executors

object FabricaJamones {
    val MAX_JAMONES = 30
    val INTER_GRANJA = 1000
    val TAMANO = 10
    val TAM_LOTE = 3
    val INTER_MENSA = 3000
    val PRIO1 = 4
    val PRIO2 = 8

    fun runFabricaSync() {
        println("Fabrica de Jamones con Monitor Synchronized")
        val secadero = SecaderoSync(TAMANO)
        val gr1 = Granja("Granja1", secadero, MAX_JAMONES, INTER_GRANJA, PRIO1)
        val gr2 = Granja("Granja2", secadero, MAX_JAMONES, INTER_GRANJA, PRIO2)
        val em = Mensajero(secadero, 2 * MAX_JAMONES / TAM_LOTE, INTER_MENSA, TAM_LOTE)

        // Crramos el pool de hilos
        val pool = Executors.newFixedThreadPool(3)
        pool.execute(gr1)
        pool.execute(gr2)
        pool.execute(em)

        // Esperamos a que acaben
        pool.shutdown()
    }

    fun runFabricaLock() {
        println("Fabrica de Jamones con Monitor Lock")
        val secadero = SecaderoLock(TAMANO)
        val gr1 = Granja("Granja1", secadero, MAX_JAMONES, INTER_GRANJA, PRIO1)
        val gr2 = Granja("Granja2", secadero, MAX_JAMONES, INTER_GRANJA, PRIO2)
        val em = Mensajero(secadero, 2 * MAX_JAMONES / TAM_LOTE, INTER_MENSA, TAM_LOTE)

        // Crramos el pool de hilos
        val pool = Executors.newFixedThreadPool(3)
        pool.execute(gr1)
        pool.execute(gr2)
        pool.execute(em)

        // Esperamos a que acaben
        pool.shutdown()

    }
}
