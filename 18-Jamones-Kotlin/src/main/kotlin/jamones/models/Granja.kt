package jamones.models

import es.joseluisgs.dam.jamones.monitor.MonitorProducerConsumer

class Granja(
    private var id: String,
    private var secadero: MonitorProducerConsumer<Jamon>,
    private var cant: Int,
    private var ms: Int,
    private var prioridad: Int
) : Thread() {

    override fun run() {
        // Cambiamos la prioridad
        priority = prioridad
        for (i in 1 until cant + 1) {
            val j = Jamon(i, id)
            println("Granja " + id + "-> Produzco Jamón: " + i + ": " + j.id + " de " + j.peso + "KG")
            secadero.put(j)
        }
        try {
            sleep(ms.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}