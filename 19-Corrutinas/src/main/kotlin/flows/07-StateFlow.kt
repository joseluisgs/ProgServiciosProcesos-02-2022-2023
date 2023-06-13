package flows

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import log

/**
StateFlow es un Flow muy particular, porque:

Maneja un único valor/objeto en su campo value
es un flujo caliente porque su instancia activa existe independientemente de la presencia de colectores.
Su valor actual se puede recuperar a través de la propiedad de valor.
Cada vez que se modifica, los recolectores asociados a él reciben una actualización
Nada más suscribirse, recibe el último valor asignado a value

Es decir  pueden tener un estado y que no dependen de un contexto específico

// Es como un monitor reactivo!!

Existen 2 variantes, StateFlow, que es inmutable (no se puede modificar value, y MutableStateFlow
En general, StateFlow sirve para almacenar un estado, y que los cambios en ese estado puedan ser escuchados de forma reactiva.

¿Te suena esto de algo? Es exactamente la definición de LiveData, pero aplicado a los Flows.
En Android, LiveData es un objeto observable que se puede usar para notificar a los observadores de los cambios en los datos.

https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/
 */

// Una nota!!!
data class Note(
    val title: String,
    val description: String,
    val type: Type = Type.TEXT,
) {
    enum class Type(val id: Int, val typeNote: String) {
        TEXT(1, "text"),
        AUDIO(2, "audio"),
    }
}

// Vamos a tener un estado para almacenar la nota activa!!!
private class EstadoState {
    // Para que no se pueda mutar desde fuera
    private var _state: MutableStateFlow<Note> =
        MutableStateFlow(Note(title = "Title 1", description = "Description 1", type = Note.Type.TEXT))

    // Creamos una bakcing properties https://kotlinlang.org/docs/properties.html#late-initialized-properties-and-variables
    val state: StateFlow<Note> = _state

    suspend fun update() {
        var count = 1
        // cada cierto tiempo el estado cambia, veremos como reaccionan los consumidores
        // Reactivamente!!!
        while (true) {
            delay(2000)
            count++
            // accedemos por el campo value
            _state.value = state.value.copy(title = "Title $count", description = "Description $count")
            log("Actualizando estado")
        }
    }
}

fun main(): Unit = runBlocking {
    val miEstado = EstadoState()
    launch {
        // lanzamos la corritina que actualiza el estado
        miEstado.update()

    }

    // Podemos leerlo o cambiarlo!!!
    miEstado.state.value.let {
        log("Estado inicial: $it")
    }

    // Recogemos reactivamente los cambios en el estado
    miEstado.state.collect {
        log("Nuevo estado ha llegado reactivamente: $it")
    }

    //podemos cambiar el estado de forma reactiva
    // val myMutableStateFlow = MutableStateFlow(1)
    // myMutableStateFlow.value = 2

    // y todos los suscriptores reaacionaria!!!

}