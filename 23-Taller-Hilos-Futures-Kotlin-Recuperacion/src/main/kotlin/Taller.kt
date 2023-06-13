import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

data class Coche(
    val tiempoReparacion: Int = (100..300).random(),
    val coste: Int = (50..200).random()
)

data class Mecanico(
    val nombre: String,
    val misCoches: List<Coche> = List((4..8).random()) { Coche() },
) {
    fun repararCoches(): Int {
        var recaudacion = 0
        println("Soy el mecánico $nombre y tengo ${misCoches.size} coches para reparar")
        measureTimeMillis {
            misCoches.forEachIndexed { index, coche ->
                println("Soy el mecánico $nombre y estoy reparando un coche $index que tarda ${coche.tiempoReparacion} ms")
                Thread.sleep(coche.tiempoReparacion.toLong())
                recaudacion += coche.coste
            }
        }.also { println("Tiempo de ejecución total mecánico $nombre: $it ms") }
        println("Soy el mecánico $nombre y he recaudado $recaudacion €")
        return recaudacion
    }
}


fun main() {
    println("MiTaller")

    // Secuencial
    val listaMecanicos: List<Mecanico> = List(3) { Mecanico("Mecanico $it") }

    println()
    println("Ejecución Secuencial")
    measureTimeMillis {
        var recaudacion = 0
        listaMecanicos.forEachIndexed { index, mecanico ->
            println("Soy el mecánico $index y comienzo a reparar")
            recaudacion += mecanico.repararCoches()
        }
        println("Recaudación total Secuencial: $recaudacion €")
    }.also { println("Tiempo de ejecución secuencial: $it ms") }

    println()

    println("Ejecución con hilos")
    // Vamos con paralelismo que sí devuelve promesas
    var pool = Executors.newFixedThreadPool(4)

    measureTimeMillis {
        var recaudacion = 0
        listaMecanicos.forEachIndexed { index, mecanico ->
            println("Soy el mecánico $index y comienzo a reparar")
            pool.execute {
                recaudacion += mecanico.repararCoches()
            }
        }
        //pool.shutdown() // termina antes???
        pool.awaitTermination(10, TimeUnit.SECONDS) // espera a que terminen todos los hilos
        pool.shutdown()
        println("Recaudación total hilos: $recaudacion €") // que pasa con este prrecio??
    }.also {
        println("Tiempo total de atención total: $it ms")
    }


    println()
    println("Ejecución con Futures")
    // Vamos con paralelismo en hilos
    pool = Executors.newFixedThreadPool(4)


    measureTimeMillis {
        var recaudacion = 0
        val futures = pool.invokeAll(
            listaMecanicos.map { mecanico ->
                Callable {
                    mecanico.repararCoches()
                }
            })

        futures.forEachIndexed { index, future ->
            println("Soy el mecánico $index y comienzo a reparar")
            recaudacion += future.get()
        }
        println("Recaudación total futures: $recaudacion €")
        pool.shutdown()
    }.also { println("Tiempo de ejecución pool futures: $it ms") }


}