package channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import log
import kotlin.coroutines.CoroutineContext

// Renombro para que no se lie con otros ficheros
data class Planet(val name: String, val volume: Long, val radius: Double, val moons: Int, val rings: Boolean)

// Creamos nuestro propio Scope, que hereda de CoroutineScope
class PlanetsProducer : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private val planets = listOf(
        Planet("Mercury", 60_827_208_742, 2_439.7, 0, false),
        Planet("Venus", 928_415_345_893, 6_051.8, 0, false),
        Planet("Earth", 1_083_206_916_846, 6_371.00, 1, false),
        Planet("Mars", 163_115_609_799, 3_389.5, 2, false),
        Planet("Jupiter", 1_431_281_810_739_360, 69_911.0, 79, true),
        Planet("Saturn", 827_129_915_150_897, 58_232.0, 83, true),
        Planet("Uranus", 68_334_355_695_584, 25_362.0, 27, true),
        Planet("Neptune", 62_525_703_987_421, 24_622.0, 14, true)
    )

    // Vamos la diferencia con el Ejem16
    // Nos ahorramos el Stream
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getChannel(): ReceiveChannel<Planet> = produce {
        planets.forEach { send(it) }
    }

    fun release() {
        this.job.cancel()
    }

}

class PlanetsConsumer : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    suspend fun processPlanetsStream(planets: ReceiveChannel<Planet>) = withContext(Dispatchers.Default) {
        planets.filterByMoons(2)
            .mapToName()
            .consumeEach { planet ->
                log("Planet consumed: $planet")
            }
    }

    private fun ReceiveChannel<Planet>.filterByMoons(moons: Int): ReceiveChannel<Planet> = produce {
        consumeEach { planet ->
            if (planet.moons >= moons)
                send(planet)
        }
    }

    private fun ReceiveChannel<Planet>.mapToName(): ReceiveChannel<String> = produce {
        consumeEach { planet -> send(planet.name) }
    }

    fun release() {
        this.job.cancel()
    }

}

fun main() {
    log("Start")

    val planetsProducer = PlanetsProducer()
    val planetsConsumer = PlanetsConsumer()

    runBlocking {
        launch {
            planetsConsumer.processPlanetsStream(planetsProducer.getChannel())
            planetsConsumer.release()
            planetsProducer.release()
        }
    }

    log("End")
}