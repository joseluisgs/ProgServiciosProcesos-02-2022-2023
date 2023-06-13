import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun log(message: String) {
    logger.debug { "(${Thread.currentThread().name}) : $message" }
}

fun log(character: Char) {
    print("$character")
}