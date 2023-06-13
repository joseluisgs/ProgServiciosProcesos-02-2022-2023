package jamones.monitor

import es.joseluisgs.dam.jamones.monitor.MonitorProducerConsumer
import jamones.models.Jamon

class SecaderoSync(private val maxJamones: Int): MonitorProducerConsumer<Jamon> {
    // El recurso
    private val secadero: MutableList<Jamon> = mutableListOf()
    // El monitor con lock de object, mejor usa el reentrant locking
    private val lock = java.lang.Object()

    private var jamonDisponible = false

    override fun get(): Jamon = synchronized(lock){
        while (secadero.size == 0) {
            try {
                // Si no hay hay sacar, esperamos,
                lock.wait()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        val jamon = secadero.removeFirst() // Saco el primero
        println("\t*El secadero tiene: " + secadero.size)
        jamonDisponible = false
        // Activamos
        lock.notifyAll()
        return jamon // DEvolvemos el jamon
    }

    override fun put(item: Jamon) = synchronized(lock) {
        while (secadero.size == maxJamones) {    // Condición de memoria limitada
            try {
                // Si no hay que producir esperamos
                lock.wait()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        secadero.add(item) //Metemos al final
        println("\t\tEl secadero tiene: " + secadero.size)
        jamonDisponible = true
        // Ya hay cantidas a consumir, activamos.
        lock.notifyAll()
    }
}
