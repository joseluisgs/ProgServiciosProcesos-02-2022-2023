import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class FabricaOther(val idFabrica: Int) {

    // Podríamos emitir hacia atrás, un histórico de coches fabricados por ejemplo 5, pero solop lo haremos con 1, el último
    private val _state: MutableSharedFlow<Coche> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val coches: Flow<Coche> = _state.asSharedFlow() // Fijate en como lo he cambiado!!!!

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

    val fabrica1 = FabricaShared(1)
    val fabrica2 = FabricaOther(2)

    // 2 SharedFlow combinados en 1 como flow!!! Mira arriba
    val coches = merge(fabrica1.coches, fabrica2.coches)

    val cadena1 = CoroutineScope(Dispatchers.IO).launch {
        var total = 0
        println("*** Cadena 1 comienza a recibir coches")
        coches
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
        println("*** cadena 2 comienza a recibir coches")
        coches
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