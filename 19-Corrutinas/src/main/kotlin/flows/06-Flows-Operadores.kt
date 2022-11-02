package flows

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import log

fun main() {
    operadorIntermedio()
    operadorTerminal()
    excepciones()
    onStart()
    onCompletion()
}

// Creamos un operador intermedio
fun Flow<Int>.squareAndFilterEven(): Flow<Int> = flow {
    collect { number ->
        val squared = number * number
        if (squared % 2 == 0)
            emit(squared)
    }
}

fun operadorIntermedio() {
    log("Start")

    val squaredEvenFlow = flowOf(8, 3, 7, 1, 0, 2, 5, 4, 6, 9).squareAndFilterEven()

    runBlocking {
        squaredEvenFlow
            .collect { squared ->
                log("$squared: This squared value is even.")
            }
    }

    log("End")
}

// Operador Terminal
suspend inline fun <reified T> Flow<T>.toArray(): Array<T> {
    val list: MutableList<T> = ArrayList()

    collect { element ->
        list.add(element)
    }

    return Array(list.size) { i ->
        list[i]
    }
}

fun operadorTerminal() {
    log("Start")

    val myFlow = flow {
        (1..10).forEach { emit(it) }
    }

    val myArray = runBlocking {
        myFlow
            .filter { number ->
                number % 2 == 0
            }
            .map { number ->
                number * number
            }
            .toArray()
    }

    myArray.forEachIndexed { index, value ->
        log("Array[$index] = $value")
    }

    log("End")
}

fun excepciones() {
    log("Start")

    val myFlow = flow {
        (1..10).forEach { number ->
            emit(number)
        }
    }

    runBlocking {
        myFlow
            .map { number ->
                if (number == 5)
                    throw IllegalStateException("Playing with exceptions on map operator.")

                number * number
            }
            .catch { e ->
                log("Catch 1: Exception Caught [ $e ]")
                throw IllegalArgumentException("Playing with exceptions on catch operator.")
            }
            .onEach { squared ->
                log("Squared: $squared")
            }
            .catch { e ->
                log("Catch 2: Exception Caught [ $e ]")
            }
            .collect()
    }

    log("End")
}

fun onStart() {
    log("Start")

    val myFlow = flowOf("Blue", "Yellow", "Red", "Green", "Orange", "Purple")

    // Si este operador está presente su bloque de código se ejecuta antes de empezar a producir y emitir elementos.
    runBlocking {
        myFlow
            .filter {
                it.endsWith('e')
            }
            .map { color ->
                "'$color' length is ${color.length}"
            }
            .onStart {
                log("------- START -------")
            }
            .collect {
                log("$it")
            }
    }

    log("End")
}

fun onCompletion() {
    log("Start")

    val myFlow = flowOf("Blue", "Yellow", "Red", "Green", "Orange", "Purple")

    /*
    Ejecuta el bloque de código tan pronto el Flow termina de emitir elementos, ya sea que haya finalizado de manera
    natural o que haya sido interrumpido por algún error. El operador onCompletion cuenta con la capacidad de detectar
    si la emisión de elementos finalizó a causa de un error, siempre y cuando la excepción haya sido lanzada desde un
    operador que le antecede, tal y como sucede con el operador catch, sin embargo no ofrece los mecanismos para manejar
    las excepciones, razón por la cual la excepción será inevitablemente propagada hacia abajo en el flujo de datos.
    Si una excepción es lanzada por uno de los operadores que se encuentran después del operador onCompletion,
    será imposible determinar si la causa de finalización fue por un error. Además si la excepción es manejada por
    un operador catch antes de alcanzar al operador onCompletion, también le será imposible determinar si la causa de
    finalización fue por un error.
     */
    runBlocking {
        myFlow
            .filter { color ->
                color.endsWith('e')
            }
            .map { color ->
                "'$color' length is ${color.length}"
            }
            .onCompletion {
                log("-------- END --------")
            }
            .collect {
                log("$it")
            }
    }

    log("End")
}