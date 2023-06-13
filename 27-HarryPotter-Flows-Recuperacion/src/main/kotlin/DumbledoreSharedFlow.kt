import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private const val MAX_SPELLS = 50

class DumbledoreSharedFlow(val character: Character) {
    // Esta definnición es la misma que StateFlow, si solo queremos uno!!!
    val _cofre: MutableSharedFlow<Spell> = MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val cofre: SharedFlow<Spell> = _cofre.asSharedFlow()

    init {
        println("\uD83E\uDDD9 ${character.name} ha llegado!")
        println("\uD83E\uDDD9 ${character.name} -> Estoy guardando hechizos en \uD83C\uDF81...")
    }

    suspend fun emitirHechizos() {
        repeat(MAX_SPELLS) { i ->
            delay((300..700).random().toLong())
            _cofre.tryEmit(
                Spell(
                    id = (i + 1),
                    name = "Hechizo ${i + 1}",
                    type = Spell.Type.values().random()
                )
            )
        }
    }
}