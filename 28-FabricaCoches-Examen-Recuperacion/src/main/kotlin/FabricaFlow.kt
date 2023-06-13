import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

// Esta es la solución especificada
// Como no sabemos cuantos coches hay que fabricar, pues vamos creando un estado con los coches reaccionamos y los emitimos


class FabricaFlow(val idFabrica: Int) {

    // con SharedFlow
    private val state: MutableSharedFlow<Coche> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)


    init {
        CoroutineScope(Dispatchers.IO).launch {
            var idCoche = 0
            while (true) {
                val coche = Coche(
                    id = ++idCoche,
                    idFabrica = idFabrica
                )
                state.tryEmit(coche)
                delay(500)
            }
        }
    }


    val coches = flow {
        state.collect {
            println("Fabrica $idFabrica emite coche: $it")
            emit(it)
        }
    }
}

fun main(args: Array<String>) = runBlocking {

    val fabrica1 = FabricaFlow(1)
    val fabrica2 = FabricaFlow(2)
    val coches = merge(fabrica1.coches, fabrica2.coches) // mergea los dos flujos

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

    // Esperamos porque llega más tarde
    delay(1000)

    val cadena2 = CoroutineScope(Dispatchers.IO).launch {
        var total = 0
        println("*** Cadena 2 comienza a recibir coches")
        coches
            // .filter { it.color == Coche.Color.NEGRO }
            // Para que se vea igual, duda de Jorge van a procesar los mismos
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