import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Las secuencias son una forma de trabajar con colecciones de datos de forma
 *  Una secuencia se puede considerar como una colección de datos que se evalúa de forma perezosa
 * y que puede tener infinitos elementos.
 * https://kotlinlang.org/docs/sequences.html
 * https://medium.com/mobile-app-development-publication/kotlin-flow-a-much-better-version-of-sequence-d2555ba9eb94
 */
fun main() = runBlocking<Unit> {
    // Cualquier colección se puede convertir en secuencia
    val secuencia = listOf(1, 2, 3, 4, 5).asSequence()

    // o crear una secuencia infinita: secuencia de numeros primos
    val numbers = generateSequence(2) { it + 1 }

    val primes = numbers.filter { number ->
        numbers.takeWhile { it <= number / 2 }.all { number % it != 0 }
    }

    println(primes.take(10).toList())

    // yield
    // Da un valor al iterador que se está construyendo y se suspende hasta que se solicita el siguiente valor.
    // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence-scope/yield.html
    val impares = sequence {
        yield(1) // me da el valor 1 y se suspende hasta que se solicite el siguiente valor
        yieldAll(
            listOf(
                3,
                5
            )
        ) // me da los valores 3 y 5 y se suspende hasta que se solicite el siguiente valor // me da el valor 1 y se suspende hast
        yieldAll(generateSequence(7) { it + 2 }) // me da una secuencia 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, ... y se suspende hasta que se solicite el siguiente valor
    }
    println(impares.take(10).toList())

    fun fibonacci() = sequence {
        var terms = Pair(0, 1)

        // this sequence is infinite
        while (true) {
            yield(terms.first)
            terms = Pair(terms.second, terms.first + terms.second)
        }
    }

    println(fibonacci().take(10).toList()) // [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]

    // Las secuencias son síncronas, es decir,
    // Y no podemos usar funciones suspendidas dentro de una secuencia, por lo tanto bloquean!!!

    launch {
        for (k in 1..3) {
            log("From main $k")
            delay(100)
        }
    }

    simple().forEach { value -> log("From simple: $value") }

    // No son fácilmente paralelizables
    run()

    // No se pueden usar en paralelo ni cancelar!
    var time = measureTimeMillis {
        simple().forEach {
            delay(300)
        }
    }
    log("Collected in $time ms")

    // Las secuencias solo pueden combinar de manera síncrona
    time = measureTimeMillis {
        firstSeq().zip(secondSeq()).forEach {
            log("Par: $it")
        }
    }
    log("Collected in $time ms")

    // Secuencias solo Sync flattening
    val startTime = System.currentTimeMillis()
    (1..3).asSequence().onEach { Thread.sleep(100) }
        .flatMap { requestSequence(it) }
        .forEach { value -> // collect and print
            log(
                "$value at ${
                    System.currentTimeMillis()
                            - startTime
                } ms from start"
            )
        }

}

fun simple(): Sequence<Int> = sequence { // sequence builder
    for (i in 1..3) {
        log("Sleeping $i")
        Thread.sleep(100) // pretend we are computing it
        log("Emitting $i")
        yield(i) // yield next value
    }
}

fun run() {
    CoroutineScope(Dispatchers.IO).launch {
        (1..3).asSequence()
            .forEach {
                log("$it ${Thread.currentThread()}")
            }
    }
}

fun firstSeq() = sequence {
    (1..3).forEach { Thread.sleep(100); yield(it) }
}

fun secondSeq() = sequence {
    (4..6).forEach { Thread.sleep(300); yield(it) }
}

fun requestSequence(i: Int): Sequence<String> = sequence {
    yield("$i: First")
    Thread.sleep(300)
    yield("$i: Second")
}
