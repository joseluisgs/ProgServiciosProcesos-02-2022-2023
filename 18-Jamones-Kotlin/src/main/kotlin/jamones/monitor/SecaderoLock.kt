package es.joseluisgs.dam.jamones.monitor

import jamones.models.Jamon
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class SecaderoLock(private val maxJamones: Int): MonitorProducerConsumer<Jamon> {
    // El recurso
    private val secadero: MutableList<Jamon> = mutableListOf()
    // Cerrojo con condiciones
    private val lock: ReentrantLock = ReentrantLock()
    private val secaderoEmptyCondition: Condition = lock.newCondition()
    private val secaderoFullCondition: Condition = lock.newCondition()


    private var jamonDisponible = false

    override fun get(): Jamon {
        lock.withLock {
            while (secadero.size == 0) {
                try {
                    // Si no hay hay sacar, esperamos a que se pueda vacia, pues lo esta,
                    secaderoEmptyCondition.await();
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            val jamon = secadero.removeFirst() // Saco el primero
            println("\t*El secadero tiene: " + secadero.size)
            jamonDisponible = false
            // Activamos avisando que hay espacio para que produzcan
            secaderoFullCondition.signalAll()
            return jamon // Devolvemos el jamon
        }
    }

    override fun put(item: Jamon) {
        lock.withLock {
            while (secadero.size == maxJamones) {    // Condición de memoria limitada
                try {
                    // Si no hay que producir esperamos, pues estamos llenos
                    secaderoFullCondition.await()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            secadero.add(item) //Metemos al final
            println("\t\tEl secadero tiene: " + secadero.size)
            jamonDisponible = true
            // Ya hay cantidad a consumir, activamos
            secaderoEmptyCondition.signalAll();
        }
    }
}
