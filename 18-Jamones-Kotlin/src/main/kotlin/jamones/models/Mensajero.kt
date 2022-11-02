package jamones.models

import es.joseluisgs.dam.jamones.monitor.MonitorProducerConsumer


class Mensajero(
    private var secadero: MonitorProducerConsumer<Jamon>,
    private val cant: Int,
    private val ms: Int,
    private val tam: Int
) : Thread() {
    override fun run() {
        val misJamones = mutableListOf<Jamon>()
        for (i in 1 until cant + 1) {
            // Mensajero saca 3 jamones
            for (k in 0 until tam) {
                val jamon: Jamon = secadero.get()
                jamon.lote = i // LE asigno el lote
                misJamones.add(jamon)
                println("Mensajero-> Paquete Lote: " + i + ": empaqueto Jamon: " + jamon.id + " " + jamon.peso + "KG de: " + jamon.idGranja)
            }
            imprimirLote(misJamones)
            misJamones.clear()
            try {
                sleep(ms.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun imprimirLote(misJamones: MutableList<Jamon>) {
        println("\t->Imprimiendo Lote")
        misJamones.forEach {
            println("\t->$it")
        }
    }
}
