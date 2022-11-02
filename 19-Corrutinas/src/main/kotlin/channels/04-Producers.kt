package channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

/**
 * El constructor produce se especializa en la creación de coroutines con la capacidad
 * de transmitir datos a través de un canal. Mientras el constructor launch retorna un Job
 * y el constructor async retorna un Deferred, cuando se crea una corotuine con el constructor produce,
 * éste retorna el ReceiveChannel que usará para la transmisión de datos.
 *
 * ES para UNO a MUCHOS, es decir, un productor y muchos consumidores y poder consumir
 * en distintos lugares
 */

fun main() = runBlocking {
    val numbers = produceNumbers() //produce enteros en un stream
    val squares = square(numbers) // realiza el cuadrado de los elemtnos que hay en el stream

    // Solo cinco veces
    repeat(5) {
        println(squares.receive()) // imprimimos los 5 primeros
    }
    println("Done!") // we are done
    coroutineContext.cancelChildren() // cancelamos las corrutinas hijas
}

// produceNumbers produce enteros y devuelve un channel de recepcion
@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) send(x++) // infinite stream of integers starting from 1
}

// Lee de un stream realiza el cuadrado y devuelve un channel de recepcion
@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    // Mandamos los cuadrados de los enteros que hay en el stream
    for (x in numbers) send(x * x)
}