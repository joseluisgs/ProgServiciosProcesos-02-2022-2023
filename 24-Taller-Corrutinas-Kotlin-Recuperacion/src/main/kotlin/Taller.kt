import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

data class Coche(
    val tiempoReparacion: Int = (100..300).random(),
    val coste: Int = (50..200).random()
)

data class Mecanico(
    val nombre: String,
    val misCoches: List<Coche> = List((4..8).random()) { Coche() },
) {
    suspend fun repararCoches(): Int {
        var recaudacion = 0
        println("Soy el mecánico $nombre y tengo ${misCoches.size} coches para reparar")
        measureTimeMillis {
            misCoches.forEachIndexed { index, coche ->
                println("Soy el mecánico $nombre y estoy reparando un coche $index que tarda ${coche.tiempoReparacion} ms")
                delay(coche.tiempoReparacion.toLong())
                recaudacion += coche.coste
            }
        }.also { println("Tiempo de ejecución total mecánico $nombre: $it ms") }
        println("Soy el mecánico $nombre y he recaudado $recaudacion €")
        return recaudacion
    }
}


fun main(): Unit = runBlocking {
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

    val myScope = CoroutineScope(Dispatchers.Default) // por probar otro

    println("Ejecución con corrutinas launch")

    measureTimeMillis {
        // val mutex = Mutex() // para evitar la condición de carrera
        var recaudacion = 0 // condición de carrera!!! Luego veremos como solucionarlo, pero es con un lock!!
        val jobs = listaMecanicos.mapIndexed { index, mecanico ->
            println("Soy el mecánico $index y comienzo a reparar")
            myScope.launch {
                // para evitar la condición de carrera
                val res = mecanico.repararCoches()
                //mutex.withLock { // para evitar la condición de carrera
                recaudacion += res
                //}
            }
        }

        // Esperamos que todos terminen
        // jobs.forEach { it.join() }
        jobs.joinAll()
        println("Recaudación total corrutinas: $recaudacion €") // que pasa con este prrecio??
    }.also {
        println("Tiempo total de atención total: $it ms")
    }


    println()
    println("Ejecución con Async/Await")
    measureTimeMillis {
        var recaudacion = 0
        val pendientes = listaMecanicos.mapIndexed { index, mecanico ->
            println("Soy el mecánico $index y comienzo a reparar")
            myScope.async {
                mecanico.repararCoches()
            }
        }

        // Esperamos que todos terminen
        pendientes.forEach { recaudacion += it.await() }
        println("Recaudación total futures: $recaudacion €")
    }.also { println("Tiempo de ejecución pool futures: $it ms") }
}