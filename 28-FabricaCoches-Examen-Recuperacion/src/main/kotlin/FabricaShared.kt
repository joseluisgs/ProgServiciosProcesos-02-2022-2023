import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

// En esta solución es un estado reactivo, que se va actualizando y los consumidores reaccionan a los cambios
// Podríamos dejarle que tenga memoria

class FabricaShared(val idFabrica: Int) {

    // Podríamos emitir hacia atrás, un histórico de coches fabricados por ejemplo 5, pero solop lo haremos con 1, el último
    private val _state: MutableSharedFlow<Coche> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val coches: SharedFlow<Coche> = _state.asSharedFlow() // Solo lectura

    init {
        CoroutineScope(Dispatchers.IO).launch {
            var idCoche = 0
            while (true) {
                val coche = Coche(
                    id = ++idCoche,
                    idFabrica = idFabrica
                )
                println("La fabrica $idFabrica ha emitido el coche: $coche")
                _state.tryEmit(coche)
                delay(500)
            }
        }
    }
}

fun main(args: Array<String>) = runBlocking {

    val fabrica = FabricaShared(1)

    val cadena1 = CoroutineScope(Dispatchers.IO).launch {
        var total = 0
        println("*** Cadena 1 comienza a recibir coches")
        fabrica.coches
            .filter { it.modelo == Coche.Modelo.SUPER && it.color == Coche.Color.AZUL }
            .take(10)
            .collect {
                println("Lista 1 recibe coche: ${++total} -> $it")
                delay(250)
            }

    }

    // Espero porque llega más tarde
    delay(1000)

    val cadena2 = CoroutineScope(Dispatchers.IO).launch {
        var total = 0
        println("*** Cadena 2 comienza a recibir coches")
        fabrica.coches
            // .filter { it.color == Coche.Color.NEGRO } // Para que se vea que consumen el mismo (a partir de que llegue)
            .filter { it.modelo == Coche.Modelo.SUPER && it.color == Coche.Color.AZUL }
            .take(10)
            .collect {
                println("Lista 2 recibe coche: ${++total} -> $it")
                delay(250)
            }

    }

    cadena1.join()
    cadena2.join()
}