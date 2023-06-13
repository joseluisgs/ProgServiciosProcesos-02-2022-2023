package flows

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import log
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

fun main() {
    ejem01()
    ejem02()
    ejem03()
    ejem04()
    ejem05()
    ejem06()
    ejem07()
    ejem08()
    ejem09()
    ejem10()
    ejem11()
    ejem12()
    ejem13()
    ejem14()
    ejem15()
    ejem16()
    ejem17()
    ejem18()
    ejem19()
    ejem20()
    ejem21()
    ejem22()
    ejem23()
    ejem24()
    ejem25()
    ejem26()
}

fun ejem01() {
    log("Start")

    // Construye un flujo de datos arbitrario con emisión secuencial llamando a la función emit dentro del bloque de código.
    val myFlow: Flow<Int> = flow {
        emit(9)
        delay(1000)
        emit(3)
        emit(10)
    }

    runBlocking {
        myFlow
            .collect { element ->
                log("$element")
            }
    }

    log("End")
}

fun ejem02() {
    log("Start")

    // Crea un Flow a partir de un conjunto de elementos establecidos como parámetros de la función.
    val myFlow: Flow<Int> = flowOf(23, 11, 9, 41, 7, 89)

    runBlocking {
        myFlow
            .collect { number ->
                log("Number: $number")
            }
    }

    log("End")
}

fun ejem03() {
    log("Start")

    // Función de extensión de varios tipos de datos que les permite ser convertidos en un Flow.
    val myList = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")
    val myFlow = myList.asFlow()

    runBlocking {
        myFlow
            .collect { planet ->
                log("Planet: $planet")
            }
    }

    log("End")
}

@OptIn(DelicateCoroutinesApi::class)
fun ejem04() {
    log("Start")

    /*
     Construye un flujo de datos arbitrario con emisión potencialmente concurrente llamando a la función send dentro
     del bloque de código. Los elementos son enviados a través del ProducerScope que es en esencia un SendChannel —
     si observas la declaración de la interfaz ProducerScope te darás cuenta de que implementa la interfaz SendChannel.
     La documentación oficial establece que la función channelFlow es thread-safe y asegura la preservación de contexto.
     Esto nos da la posibilidad de producir elementos desde varias fuentes de manera concurrente y desde contextos distintos.
     */
    val myFlow = channelFlow {
        launch(Dispatchers.Default) {
            (1..10).forEach { number ->
                delay(250)
                send(number)
            }
        }

        launch(newSingleThreadContext("MyThread")) {
            send(109)
            delay(500)
            send(289)
            delay(750)
            send(150)
        }
    }

    runBlocking {
        myFlow
            .collect { number ->
                log("Number: $number")
            }
    }

    log("End")
}

@OptIn(DelicateCoroutinesApi::class)
fun ejem05() {
    log("Start")

    val myFlow: Flow<String> = flow {
        log("${currentCoroutineContext()[CoroutineName]} -> Flow start")
        val helloText = "Hello"
        log("${currentCoroutineContext()[CoroutineName]} -> Emitting: \"$helloText\"...")
        emit(helloText)
        delay(1000)
        val flowText = "Hello"
        log("${currentCoroutineContext()[CoroutineName]} -> Emitting: \"$flowText\"...")
        emit(flowText)
        log("${currentCoroutineContext()[CoroutineName]} -> Flow end")
    }

    runBlocking {
        launch(Dispatchers.Default + CoroutineName("Red Coroutine")) {
            myFlow.collect { element ->
                log("${coroutineContext[CoroutineName]} -> Received: $element")
            }
        }

        launch(newSingleThreadContext("MyThread") + CoroutineName("Blue Coroutine")) {
            myFlow
                .collect { element ->
                    log("${coroutineContext[CoroutineName]} -> Received: $element")
                }
        }
    }

    log("End")
}

fun ejem06() {
    log("Start")

    val myFlow = flowOf(2, 4, 9, 1, 5, 7, 6, 0, 8, 3)

    runBlocking {
        // Permite transformar los elementos que recibe desde el flujo de datos para luego encauzar hacia el flujo de datos los elementos ya modificados.
        myFlow
            .map { number ->
                "Squared Value: ${number * number}"
            }
            .collect {
                log(it)
            }
    }

    log("End")
}

fun ejem07() {
    log("Start")

    val myFlow = flowOf("Lorem ipsum dolor sit amet", "consectetur adipiscing elit")

    runBlocking {
        /*
        Al igual que el operador map, este operador permite transformar los elementos que recibe desde el flujo de datos
        para luego encauzar hacia el flujo de datos los elementos ya modificados. A diferencia del operador map que por
        cada elemento que recibe puede encauzar solamente un elemento, el operador transform puede encauzar más de un
        elemento por cada elemento recibido.
         */
        myFlow
            .transform { text ->
                val words = text.split(" ")
                for (word in words) {
                    emit(word)
                }
            }
            .collect { word ->
                log(word)
            }
    }

    log("End")
}

@OptIn(FlowPreview::class)
fun ejem08() {
    log("Start")

    val flowMultipliesOfTwo = flowOf(2, 4, 6)
    val flowMultiplesOfThree = flowOf(3, 6, 9)
    val flowMultiplesOfFive = flowOf(5, 10, 15)

    val myFlow = flowOf(flowMultipliesOfTwo, flowMultiplesOfThree, flowMultiplesOfFive)

    runBlocking {
        /*
         A partir de un Flow de Flows Flow<Flow<T>> se procesa cada Flow y se produce un Flow de un solo nivel que
         contiene todos los elementos de cada uno de los Flow, encauzando cada elemento nuevamente hacia el flujo de datos.
         La función flattenMerge acepta un parámetro opcional llamado concurrency que indica la cantidad máxima de Flows
         que podrán ser procesados concurrentemente. Si no se le indica ningún valor, tomará el valor por defecto correspondiente
         a la constante DEFAULT_CONCURRENCY (16
         */
        myFlow
            .flattenMerge()
            .collect { number ->
                log("Number: $number")
            }
    }

    log("End")
}

@OptIn(FlowPreview::class)
fun ejem09() {
    log("Start")

    val flowMultipliesOfTwo = flowOf(2, 4, 6)
    val flowMultiplesOfThree = flowOf(3, 6, 9)
    val flowMultiplesOfFive = flowOf(5, 10, 15)

    val myFlow = flowOf(flowMultipliesOfTwo, flowMultiplesOfThree, flowMultiplesOfFive)

    runBlocking {
        /*
         El operador flatMapMerge es un atajo de aplicar los operadores map y flattenMerge (map(transform).flattenMerge(concurrency)).
        */
        myFlow
            .flatMapMerge { f ->
                flow {
                    f.collect { number ->
                        emit(number * number)
                    }
                }
            }
            .collect { squared ->
                log("Squared: $squared")
            }
    }

    log("End")
}

fun ejem10() {
    log("Start")

    val mySet = setOf("Blue", "Yellow", "Red", "Green", "Orange", "Purple")
    val myFlow = mySet.asFlow()

    runBlocking {
        // ermite aplicar un filtro a los elementos que recibe desde el flujo de datos para luego volver a encauzar los
        // elementos que superaron la condición de filtrado, hacia el flujo de datos.
        myFlow
            .filter {
                it.endsWith('e')
            }
            .collect {
                log(it)
            }
    }

    log("End")
}

fun ejem11() {
    log("Start")

    val myFlow = flowOf(3, 5, 1, 7, 7, 7, 1, 4, 4, 4, 3, 8, 8, 9)

    runBlocking {
        /*
        Desecha todos los elementos recibidos que son iguales al elemento más reciente. Esto quiere decir que si se
        emite el mismo elemento de manera consecutiva, solamente dejará pasar el elemento la primera vez desechando los
         demás elementos que se reciban hasta que el elemento recibido sea distinto.
         */
        myFlow
            .distinctUntilChanged()
            .collect { number ->
                log("Number: $number")
            }
    }

    log("End")
}


fun ejem12() {
    log("Start")

    val myFlow = channelFlow {
        launch {
            (1..100).forEach {
                delay(1000)
                send(it)
            }
        }

        launch {
            listOf(11, 22, 33, 44, 55, 66, 77, 88, 99).forEach {
                delay(500)
                send(it)
            }
        }
    }

    runBlocking {
        /*
         Deja pasar los n primeros elementos que recibe desde el flujo de datos.
         Cancela la emisión de datos cuando se ha alcanzado la cantidad de elementos especificados como parámetro.
         */
        myFlow
            .take(5)
            .collect {
                log("$it")
            }
    }

    log("End")
}

fun ejem13() {
    log("Start")

    val flow1 = flowOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")
    val flow2 = flow {
        (11..1000 step 11).forEach {
            delay(250)
            emit(it)
        }
    }

    runBlocking {
        /*
        Combina pares de elementos obtenidos desde dos flujos de datos diferentes, de manera intercalada, es decir que por
        cada elemento recibido desde uno de los Flows, se debe esperar la emisión de otro elemento desde el otro Flow
        para ejecutar el bloque de código. Dentro del bloque de código se procesan los elementos para obtener un nuevo
        elemento que será encauzando hacia el flujo de datos. El operador se mantendrá en ejecución hasta que uno de los dos
        Flows finalice la emisión de elementos, cancelando inmediatamente el otro Flow.
         */
        flow1
            .zip(flow2) { planet, number ->
                "$number - $planet"
            }
            .collect {
                log(it)
            }
    }

    log("End")
}

fun ejem14() {
    log("Start")

    val planets = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")
    val flow1 = flow {
        planets.forEach { planet ->
            delay(400)
            emit(planet)
        }
    }

    val flow2 = flow {
        (1..10).forEach {
            delay(250)
            emit(it)
        }
    }

    runBlocking {
        /*
         Combina pares de elementos obtenidos desde dos flujos de datos diferentes tan pronto son recibidos,
         es decir que cada elemento recibido desde uno de los Flows se combina con el elemento más reciente recibido
         desde el otro Flow e inmediatamente se ejecuta el bloque de código. Dentro del bloque de código se procesan
         los elementos para obtener un nuevo elemento que será encauzado hacia el flujo de datos. El operador se mantendrá
         en ejecución hasta que ambos Flows finalicen la emisión de elementos.
         */
        flow1
            .combine(flow2) { planet, number ->
                "$number - $planet"
            }
            .collect {
                log(it)
            }
    }

    log("End")
}

fun ejem15() {
    log("Start")

    val myFlow = flowOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")

    val time = measureTimeMillis {
        runBlocking {
            /**
             * Es un paso intermedio que permite realizar operaciones con los elementos que se están emitiendo
             * para luego encauzarlos íntegros nuevamente hacia el flujo de datos.
             */
            myFlow
                .onEach { planet ->
                    delay(200)
                    log("OnEach: $planet")
                }
                .collect { planet ->
                    delay(700)
                    log("Collect: $planet")
                }
        }
    }

    log("-----------------")
    log("Total Time: $time")
    log("-----------------")

    log("End")
}

fun ejem16() {
    log("Start")

    val myFlow = flowOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")

    val time = measureTimeMillis {
        runBlocking {
            /*
            Cambia el contexto de la coroutine en el que se ejecutan todos los operadores que le preceden.
            Si el CoroutineContext especificado contiene un Job, se lanza una excepción de tipo IllegalArgumentException.
            Si al especificar el contexto se cambia de Dispatcher, se rompe la naturaleza secuencial en la ejecución dentro
            del flujo de datos.
             */
            myFlow
                .onEach { planet ->
                    delay(200)
                    log("OnEach: $planet")
                }
                .flowOn(Dispatchers.Default)
                .collect { planet ->
                    delay(700)
                    log("Collect: $planet")
                }
        }
    }

    log("-----------------")
    log("Total Time: $time")
    log("-----------------")

    log("End")
}

fun ejem17() {
    log("Start")

    val myFlow = flowOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")

    val time = measureTimeMillis {
        runBlocking {
            /*
            Permite la ejecución concurrente dentro del flujo de datos dividiéndolo en dos partes.
            La coroutine que llama al operador terminal se adjudica la ejecución del flujo de datos desde el operador
            buffer hasta el operador terminal. La ejecución del flujo de datos por encima del operador buffer es
            ejecutada por una nueva coroutine. El argumento n indica el tamaño específico de elementos que se desea
            que se procesen de manera concurrente. Si no se especifica un tamaño, el buffer tendrá el tamaño por
            defecto que equivale a Channel.BUFFERED (64). Otros valores que se pueden asignar por medio de constantes
            son los siguientes: Channel.CONFLATED, Channel.RENDEZVOUS y Channel.UNLIMITED. Este es el operador necesario
            para controlar el comportamiento en casos en el que la frecuencia de emisión de elementos sobrepasa la
            frecuencia en la que el consumidor los procesa. A esta dinámica se le conoce como ‘Backpressure’ y al aplicar
            la técnica adecuada se mejora el rendimiento y la eficacia del Flow.
             */
            myFlow
                .onEach { planet ->
                    delay(100)
                    log("OnEach: $planet")
                }
                .buffer()
                .collect { planet ->
                    delay(700)
                    log("Collect: $planet")
                }
        }
    }

    log("-----------------")
    log("Total Time: $time")
    log("-----------------")

    log("End")
}

fun ejem18() {
    log("Start")

    val myFlow = flowOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")

    val time = measureTimeMillis {
        runBlocking {
            /*
            : El operador conflate es un atajo del operador buffer con capacidad Channel.CONFLATED (buffer(Channel.CONFLATED)).
            Si la coroutine que ejecuta el código que está por debajo del operador no puede seguirle el paso a la coroutine
            que ejecuta el código que está por encima del operador, se va desechando el exceso de elementos manteniendo
            siempre el más reciente en el buffer.
             */
            myFlow
                .onEach { planet ->
                    delay(200)
                    log("OnEach: $planet")
                }
                .conflate() // buffer(Channel.CONFLATED)
                .collect { planet ->
                    delay(700)
                    log("Collect: $planet")
                }
        }
    }

    log("-----------------")
    log("Total Time: $time")
    log("-----------------")

    log("End")
}

fun ejem19() {
    log("Start")

    val myFlow = flow {
        (1..10).forEach { number ->
            emit(number)
        }
    }

    runBlocking {
        myFlow
            .map { number ->
                number * number
            }
            .onEach { squared ->
                log("Squared: $squared")
            }
            .flowOn(Dispatchers.Default)
            .collect()
        // Activa el flujo de datos y procesa los elementos recibidos según se vayan recibiendo.
    }

    log("End")
}

fun ejem20() {
    log("Start")

    val myFlow = flow {
        emit(1)
        delay(200)
        emit(2)
        delay(200)
        emit(3)
        delay(300)
        emit(4)
        delay(200)
        emit(5)
    }

    runBlocking {
        myFlow
            .collectLatest { number ->
                log("Before delay: $number")
                delay(250)
                log("After delay: $number")
            }
        // Activa el flujo de datos y procesa los elementos según se vayan recibiendo, cancelando el procesamiento del último elemento recibido si éste aún está siendo procesado.
    }

    log("End")
}

fun ejem21() {
    log("Start")

    val myFlow = flow {
        emit("Hello Flow!")
    }

    /*
     Espera recibir solamente un elemento. Si no se emite ningún elemento, lanza una excepción de tipo NoSuchElementException.
     Si se emite más de un elemento, lanza una excepción de tipo IllegalStateException.
     */
    runBlocking {
        val data = myFlow.single()
        log(data)
    }

    log("End")
}

fun ejem22() {
    log("Start")

    val myFlow = flowOf(3, 9, 5, 1, 2)

    /*
    Activa el Flow y procesa el primer elemento que es emitido, cancelando el Flow después de haberlo recibido.
     Si el Flow no contiene elementos se lanza una excepción de tipo NoSuchElementException.
     */
    runBlocking {
        val firstElement = myFlow.first()
        log("First Element: $firstElement")
    }

    log("End")
}

const val FACTORIAL_SUBJECT = 10

fun ejem23() {
    log("Start")

    val myFlow = flow {
        (1..FACTORIAL_SUBJECT).forEach {
            emit(it)
        }
    }
    /*
    Procesa los elementos uno por uno según se vayan recibiendo. La variable accumulator se inicializa con el primer
    elemento recibido y a partir de ahí por cada elemento recibido se le aplica la operación definida dentro de su
    bloque de código manteniendo siempre el nuevo resultado en la variable accumulator.
     */
    runBlocking {
        val factorial = myFlow.reduce { accumulator, value ->
            accumulator * value
        }
        log("$FACTORIAL_SUBJECT! = $factorial")
    }

    log("End")
}

fun ejem24() {
    log("Start")

    val myFlow = flow {
        emit("Mercury")
        emit("Venus")
        emit("Earth")
        emit("Mars")
        emit("Jupiter")
        emit("Saturn")
        emit("Uranus")
        emit("Neptune")
    }

    runBlocking {
        val planets = myFlow
            .filter { planet -> planet.length > 5 }
            .toList()
        // Retorna una lista con los elementos que fueron recibidos.

        log("$planets")
    }

    log("End")
}

fun ejem25() {
    log("Start")

    val myScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = Job() + Dispatchers.Default
    }

    val myFlow = flow {
        (1..5).forEach { number ->
            emit(number)
        }
    }

    /**
     * Inicia una coroutine en el Scope especificado como parámetro e inmediatamente activa el Flow.
     * Retorna un Job tal y como lo hace el constructor de coroutines launch.
     */
    runBlocking {
        val job1 = myFlow
            .onEach { number ->
                delay(150)
                log("Number: $number")
            }
            .launchIn(this)

        val job2 = myFlow
            .onEach { number ->
                delay(250)
                log("Number: $number")
            }
            .launchIn(myScope)

        joinAll(job1, job2)
        log("Flows are done emitting!")
    }

    myScope.cancel()

    log("End")
}

@OptIn(FlowPreview::class)
fun ejem26() {
    log("Start")

    val myScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = Job() + Dispatchers.Default
    }

    val myFlow = flowOf("Blue", "Red", "Yellow")

    /**
     * : Inicia una coroutine en el Scope especificado como parámetro e inmediatamente activa el Flow.
     * Retorna un ReceiveChannel tal y como lo hace el constructor de coroutines produce.
     */
    runBlocking {
        val channel1 = myFlow
            .onEach { planet ->
                log("OnEach: $planet")
            }
            .produceIn(this)

        val channel2 = myFlow
            .onEach { planet ->
                log("OnEach: $planet")
            }
            .produceIn(myScope)

        val job1 = launch {
            for (planet in channel1) {
                delay(150)
                log("Channel 1: $planet")
            }
        }

        val job2 = launch {
            for (planet in channel2) {
                delay(250)
                log("Channel 2: $planet")
            }
        }

        joinAll(job1, job2)
        log("Flows are done emitting!")
    }

    myScope.cancel()

    log("End")
}