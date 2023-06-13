package flows

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import log

/**
 * Los Flows (Flujos) es una forma de obtener y procesar datos de forma asíncrona.
 * Flow provee un flujo de datos “en frío” — ‘cold stream’ en Inglés — . ¿Qué quiere decir ésto?
 * En una estructura de flujo de datos “en frío”, los elementos son producidos y emitidos bajo demanda.
 * Ésto quiere decir que los elementos se empiezan a producir y emitir hasta que el consumidor lo requiera y
 * actúan de manera asíncrona. Un flujo de datos Flow consta de una estructura de 3 partes:
 * Creación del flujo de datos
 * Operadores intermedios
 * Operadores terminales
 * https://devexperto.com/flows-kotlin/
 * https://kotlinlang.org/docs/flow.html#flows
 * https://www.baeldung.com/kotlin/flow-intro
 * https://medium.com/mobile-app-development-publication/kotlin-flow-a-much-better-version-of-sequence-d2555ba9eb94
 */

// Una función que produce un flujo de datos
fun simple(): Flow<Int> = flow { // flow builder
    for (i in 1..3) {
        delay(200) // Operación que podría tardar!!!
        emit(i) // emitimos el valor
    }
}

fun main() = runBlocking<Unit> {
    // Lunzamos una corrutina para ver que nos e ha quedado bloqueada como las secuencias...
    launch {
        for (k in 1..3) {
            log("No estoy bloqueado: $k")
            delay(100)
        }
    }

    // Comsumimos, collect el flujo, de manera asincrona, como podemos ver no estamos bloqueados
    simple().collect { value -> log("$value") }
    log("FIN1")

    // Podemos hacer lo con el lambda flow, como ves no necesitamos una corrutina, ya que el flow es
    // asincrono y no bloquea el hilo
    val flow = flow {
        for (i in 1..3) {
            emit(i) // emitimos
            delay(1000)
        }
        emit(4)
        delay(1000)
        emit(5)
    }.filter { it > 2 } // Operador intermedio que filtra los valores
        .map { it * 2 } // Operador intermedio que multiplica por 2 los valores
        .transform { if (it > 4) emit("Item: $it") } // Para trasformaciones más complejas

    // Cada que te conectas a un nuevo flujo, comienza desde el principio para dicho consumidor.
    // Es una de sus características de los flujos, que no tendrán los canales.

    // Un consumidor 1
    launch {
        delay(2000)
        flow.collect { value ->
            log("Consumidor 1: $value")
        }
    }
    // Un consumidor 2
    launch {
        flow.collect { value ->
            delay(500)
            log("Consumidor 2: $value")
        }
    }
    // Un consumidor 3
    launch {
        delay(500)
        // podemos enviar las emisiones de flujo a través de un canal de una capacidad específica y hace funcionar el colector en una corrutina separada.
        // https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/buffer.html
        flow.buffer()
            .collect { value ->
                log("Consumidor 3: $value")
            }
    }

    log("FIN2")

    // Podemos usar el builder flowOf para crear un flujo de datos
    val flow1 = flowOf(1, 2, 3, 4, 5).onEach { delay(300) }
    val flow2 = flowOf("a", "b", "c", "d", "e").onEach { delay(500) }

    // Podemos usar el zip para combinar los flujos de datos
    flow1.zip(flow2) { a, b -> "$a->$b" }.collect { log(it) } // Van a la par, uno espera al otro

    flow1.combine(flow2) { a, b -> "$a->$b" }.collect { log(it) } // Casa el que tenga en ese momento...

    flow1.zip(flow2) { a, b -> "$a->$b" }.toList().forEach { log("Lista:$it") } // Van a la par

    flow1.combine(flow2) { a, b -> "$a->$b" }.toList()
        .forEach { log("Lista:$it") } // Casa el que tenga en ese momento...

    log("FIN4")

    // Los flows no se pueden cambiar de contexto
    val flow3 = flow {
        //withContext(Dispatchers.IO) { // Excepción!!! si no pongo floOn
        for (i in 1..3) {
            emit(i)
            delay(1000)
        }
        // podemos lanzar excepciones
        throw IllegalStateException("Error Flujo")
        //}
    }

    // Ahora si podemos cambiar de contexto y recoger excepcuibes
    flow3.flowOn(Dispatchers.IO)
        .catch {
            log("Error en Flow3: $it")
        }
        .collect { value ->
            log("Recolectamos de Flow3: $value")
        }

    log("FIN5")
}