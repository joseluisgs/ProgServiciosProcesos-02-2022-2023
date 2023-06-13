import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take

fun main(args: Array<String>) = runBlocking {
    println("Bienvenidos a Harry Potter Flows")

    //dumbledoreFlow()
    //dumbledoreStateFlow()
    dumbledoreSharedFlow()

}

suspend fun dumbledoreSharedFlow() {
    println()
    println("Dumbledore Shared Flow ...")
    val dumbledore = DumbledoreStateFlow(Character.DUMBLEDORE)

    val dumbledoreJ = CoroutineScope(Dispatchers.IO).launch {
        dumbledore.emitirHechizos()
    }

    val harryJ = CoroutineScope(Dispatchers.IO).launch {
        delay((1000..3000).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Harry Potter -> Estoy listo para recibir hechizos \uD83D\uDE4C\uD83C\uDFFB")
        dumbledore.cofre
            .filter { spell -> spell.type == Spell.Type.ATTACK }
            .take(3)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Harry Potter -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Hermione Granger
    val grangerJ = CoroutineScope(Dispatchers.IO).launch {
        delay((200..500).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Hermione Granger -> Conozco el hechizo \uD83D\uDCA5 Granger")
        dumbledore.cofre
            .filter { spell -> spell.type == Spell.Type.HEAL }
            .take(3)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Hermione Granger -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Ron Weasley
    val weasleyJ = CoroutineScope(Dispatchers.IO).launch {
        delay((200..500).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Ron Weasley -> Conozco el hechizo \uD83D\uDCA5 Weasley")
        dumbledore.cofre
            .filter { spell -> spell.type == Spell.Type.DEFENSE }
            .take(5)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Ron Weasley -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Esperamos a que todos terminen
    harryJ.join()
    grangerJ.join()
    weasleyJ.join()
    dumbledoreJ.join()
}

suspend fun dumbledoreStateFlow() {
    println()
    println("Dumbledore State Flow ...")
    val dumbledore = DumbledoreStateFlow(Character.DUMBLEDORE)

    val dumbledoreJ = CoroutineScope(Dispatchers.IO).launch {
        dumbledore.emitirHechizos()
    }

    val harryJ = CoroutineScope(Dispatchers.IO).launch {
        delay((1000..3000).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Harry Potter -> Estoy listo para recibir hechizos \uD83D\uDE4C\uD83C\uDFFB")
        dumbledore.cofre
            .filter { spell -> spell.type == Spell.Type.ATTACK }
            .take(3)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Harry Potter -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Hermione Granger
    val grangerJ = CoroutineScope(Dispatchers.IO).launch {
        delay((200..500).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Hermione Granger -> Estoy listo para recibir hechizos \uD83D\uDE4C\uD83C\uDFFB")
        dumbledore.cofre
            .filter { spell -> spell.type == Spell.Type.HEAL }
            .take(3)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Hermione Granger -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Ron Weasley
    val weasleyJ = CoroutineScope(Dispatchers.IO).launch {
        delay((200..500).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Ron Weasley -> Estoy listo para recibir hechizos \uD83D\uDE4C\uD83C\uDFFB")
        dumbledore.cofre
            .filter { spell -> spell.type == Spell.Type.DEFENSE }
            .take(5)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Ron Weasley -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Esperamos a que todos terminen
    harryJ.join()
    grangerJ.join()
    weasleyJ.join()
    dumbledoreJ.join()
}

suspend fun dumbledoreFlow() {
    println()
    println("DumbleDore Flow ...")
    val dumbledore = DumbledoreFlow(Character.DUMBLEDORE)

    // Creamos a los demas personajes y los suscribimos al flow con lo que emite Dumbledore
    // Harry Potter
    val harryJ = CoroutineScope(Dispatchers.IO).launch {
        delay((1000..3000).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Harry Potter -> Listo para coger mis hechizos \uD83D\uDCA5 ...")
        dumbledore.emitirHechizos()
            .filter { spell -> spell.type == Spell.Type.ATTACK }
            .take(3)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Harry Potter -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Hermione Granger
    val grangerJ = CoroutineScope(Dispatchers.IO).launch {
        delay((200..500).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Hermione Granger -> Listo para coger mis hechizos \uD83D\uDCA5 ...")
        dumbledore.emitirHechizos()
            .filter { spell -> spell.type == Spell.Type.HEAL }
            .take(3)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Hermione Granger -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Ron Weasley
    val weasleyJ = CoroutineScope(Dispatchers.IO).launch {
        delay((200..500).random().toLong())
        println("\uD83D\uDE4B\uD83C\uDFFB Ron Weasley -> Listo para coger mis hechizos \uD83D\uDCA5 ...")
        dumbledore.emitirHechizos()
            .filter { spell -> spell.type == Spell.Type.DEFENSE }
            .take(5)
            .collect { spell ->
                delay((200..500).random().toLong())
                println("\uD83D\uDE4B\uD83C\uDFFB Ron Weasley -> Conozco el hechizo \uD83D\uDCA5 ${spell.name} y lo voy a usar por que es de tipo ${spell.type}")
            }
    }

    // Esperamos a que todos terminen
    harryJ.join()
    grangerJ.join()
    weasleyJ.join()
}


