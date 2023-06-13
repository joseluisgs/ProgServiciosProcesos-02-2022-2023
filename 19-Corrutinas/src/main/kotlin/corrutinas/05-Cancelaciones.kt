import kotlinx.coroutines.*

// https://kotlinlang.org/docs/cancellation-and-timeouts.html

fun main() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            log("job: I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(300L) // delay a bit
    log("main: I'm tired of waiting!")
    // Comenta yna
    //job.cancel() // cancels the job
    // job.join()
    job.cancelAndJoin() // cancels the job and waits for its completion
    log("main: Now I can quit.")

    // ES asincrono, no se bloquea
    // Si queremos que devuelva null, usamos withTimeoutOrNull
    // si no queremos usamos withTimeout con try/catch
    val result = withTimeoutOrNull(300L) {
        repeat(1000) { i ->
            log("I'm sleeping $i ...")
            delay(500L)
        }
        "Done" // will get cancelled before it produces this result
    }
    log("Result is $result")
}