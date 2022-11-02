package channels

import flows.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
Los Canales nos sirven para compartir información entre corrutinas, de manera
que es como una cola bloqueante,
por lo que una vez se recolecta un valor, este deja de estar disponible para otros consumidores.
Un Channel es una estructura de datos que permite la comunicación entre coroutines.
La ventaja de usar un Channel es que el control de la lectura y escritura ya está controlado
 y soportado dentro de su estructura, es decir, un Channel es thread-safe por lo tanto
 la implementación de nuestro programa será más simple. Se comunican por el paso de mensajes de
 los métodos send y receive. Debes saber que la función send es una suspend function que opera
 en conjunto con la función receive que también es una suspend function. Cuando no se le establece
 un tamaño al canal, la transmisión se da solamente hasta que se han invocado ambas funciones.
 Esto quiere decir que si la función send es invocada, el hilo se suspenderá hasta que la función
 receive sea invocada y viceversa. A esta dinámica se le conoce como rendezvous.

https://kotlinlang.org/docs/channels.html
Hasta que no se trata un valor enviado no se puede enviar el siguiente
Lo podemos cambiar con Capacity

TIPO DE CANALES: Channel,
RENDEZVOUS -> RendezvousChannel(): 0  los elementos son enviados únicamente cuando un emisor y un receptor se encuentran.
UNLIMITED -> LinkedListChannel() Con este Channel, tenemos un buffer con capacidad ilimitada, lo que de la memoria
CONFLATED -> ConflatedChannel() siempre ofrece el último valor enviado al buffer, descartando los que no fueron recibidos por nadie. El receptor siempre obtiene el elemento que se envió más recientemente.
BUFFERED -> ArrayChannel(CHANNEL_DEFAULT_CAPACITY) La capacidad del buffer de este Channel es de 64 elementos, y esa es la capacidad default configurada en la JVM (podemos cambiarla, pero no lo necesitamos).
Se bloqueará la producción si está lleno

onBufferOverflow, DROP_OLDEST descarta los que no se han podido consumir
DROP_LATEST descarta los nuevos y SUSPEND suspende hasta que se pueda consumir
*/

class EstadoChannel {
    private var _state = Channel<Note>() // Buffer de 3 elementos
    val state = _state //.receiveAsFlow() // asi se verá como un flow

    @OptIn(ExperimentalCoroutinesApi::class)
    // función de producción
    suspend fun update() {
        var count = 1
        while (!_state.isClosedForSend) {
            delay(500)
            count++
            println("Emitiendo: Title $count")
            _state.send(Note(title = "Title $count", description = "Description $count"))
            if (count > 6) {
                _state.close()
            }
        }
    }
}

fun main(): Unit = runBlocking {
    val miEstado = EstadoChannel()
    // Lanzamos a producir
    launch {
        miEstado.update()

    }

    // Lanzamos a consumior
    for (state in miEstado.state) {
        delay(1000)
        println("Recolectando: $state")
    }
//    viewModel.state.collect {
//        delay(1000)
//        println("Recolectando: $it")
//    }
}