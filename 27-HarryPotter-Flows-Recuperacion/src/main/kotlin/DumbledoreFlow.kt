import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private const val MAX_SPELLS = 50

class DumbledoreFlow(val character: Character) {

    init {
        println("\uD83E\uDDD9 ${character.name} ha llegado!")
        println("\uD83E\uDDD9 ${character.name} -> Estoy guardando hechizos en \uD83C\uDF81...")
    }

    suspend fun emitirHechizos(): Flow<Spell> = flow {
        repeat(MAX_SPELLS) { i ->
            delay((300..700).random().toLong())
            emit(
                Spell(
                    id = (i + 1),
                    name = "Hechizo ${i + 1}",
                    type = Spell.Type.values().random()
                )
            )
        }

    }
}