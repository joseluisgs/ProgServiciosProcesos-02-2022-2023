import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private const val MAX_SPELLS = 50

class DumbledoreStateFlow(val character: Character) {

    private val _cofre: MutableStateFlow<Spell> = MutableStateFlow(Spell(0, "Hechizo 0", Spell.Type.NONE))
    val cofre: StateFlow<Spell> = _cofre.asStateFlow()

    init {
        println("\uD83E\uDDD9 ${character.name} ha llegado!")
        println("\uD83E\uDDD9 ${character.name} -> Estoy guardando hechizos en \uD83C\uDF81...")
    }

    suspend fun emitirHechizos() {
        repeat(MAX_SPELLS) { i ->
            delay((300..700).random().toLong())
            _cofre.value = Spell(
                id = (i + 1),
                name = "Hechizo ${i + 1}",
                type = Spell.Type.values().random()
            )
        }
    }
}