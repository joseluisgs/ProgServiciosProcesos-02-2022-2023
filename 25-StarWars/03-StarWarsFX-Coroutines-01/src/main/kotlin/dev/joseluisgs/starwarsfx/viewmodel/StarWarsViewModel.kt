package dev.joseluisgs.starwarsfx.viewmodel

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.joseluisgs.starwarsfx.factories.DroideGenerator
import dev.joseluisgs.starwarsfx.locale.toLocalNumber
import dev.joseluisgs.starwarsfx.models.Defensa
import dev.joseluisgs.starwarsfx.models.Droide
import dev.joseluisgs.starwarsfx.models.DroideW447
import dev.joseluisgs.starwarsfx.repositories.matrix.Matrix
import dev.joseluisgs.starwarsfx.repositories.pila.Pila
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.delay
import mu.KotlinLogging
import java.io.File


private val logger = KotlinLogging.logger {}


private const val efectoDisparoNormal = 25
private const val efectoDisparoCritical = 50

data class StarWarsError(val message: String)

sealed class StarWarsResultado {
    data class StarWarsFin(val message: String) : StarWarsResultado()
    object StarWarsLoading : StarWarsResultado()
}

// Ahora al sar corrutinas no hace falta poner aquín los run later :)

class StarWarsViewModel(
    private val droideGenerator: DroideGenerator
) {
    // Creo el estado con la imagen inicial
    val state = StarWarsState()
    private var enemigos = Pila<Droide>()
    private var espacio: Matrix<Droide> = Matrix(0, 0)

    init {
        logger.info { "StarWarsViewModel cargando datos" }
    }

    suspend fun comenzarSimulacion(dimensionEspacio: Int, tiempo: Int, droidesTotal: Int) {
        logger.info { "Comenzando simulación" }

        printOperacion("Comenzando simulación")

        // estado inicial
        // estado inicial
        state.tiempoActual.value = 0
        state.cuadrante.value = ""
        state.muertos.value = 0
        state.aciertos.value = 0
        state.disparos.value = 0
        state.dimensionEspacio.value = dimensionEspacio
        state.tiempoTotal.value = tiempo
        state.droidesTotal.value = droidesTotal
        state.resultado.value = Ok(StarWarsResultado.StarWarsLoading)

        initDroidesEnemigos()
        colocarDroidesEnemigos()

        simulacion()

        estadoResultadoFinal()
    }

    private fun colocarDroidesEnemigos() {
        logger.debug { "Colocando droides enemigos" }

        printOperacion("Colocando droides enemigos")

        // creamos la matriz de droides con la dimensión del espacio
        espacio = Matrix(state.dimensionEspacio.value, state.dimensionEspacio.value)
        // La imprimimos
        logger.debug { "Espacio generado: \n$espacio" }
        // Colocamos los droides de manera aleatoria siempre y cuando no haya otro droide
        for (i in 0 until enemigos.size()) {
            var fil: Int
            var col: Int
            // si esta vivo
            if (enemigos[i].isAlive) {
                do {
                    fil = (0 until state.dimensionEspacio.value).random()
                    col = (0 until state.dimensionEspacio.value).random()
                } while (espacio[fil, col] != null)
                espacio[fil, col] = enemigos[i]
            }
        }

        logger.debug { "Droides enemigos colocados: ${enemigos.size()}" }

        // imprimir la matriz
        printCuadrante()
    }

    private fun initDroidesEnemigos() {
        logger.debug { "Generando droides enemigos" }
        state.operacion.value = state.operacion.value + "\nGenerando droides enemigos"
        enemigos = Pila<Droide>()
        for (i in 1..state.droidesTotal.value) {
            enemigos.push(droideGenerator.randomDroide())
        }
        logger.debug { "Droides enemigos generados: ${enemigos.size()}" }
    }

    private fun matrixToArea(): String {
        val sb = StringBuilder()
        for (i in 0 until espacio.rows) {
            for (j in 0 until espacio.cols) {
                if (espacio[i, j] != null) {
                    sb.append("[ ").append(espacio[i, j]?.toArea() ?: " ").append(" ]")
                } else {
                    sb.append("[   ]")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    // Este métdo va a estar en background, no olvides que si tocas algo de la interfaz
    // Debes usar Platform.runLater y ademas debes lanzar este método con un hilo
    private suspend fun simulacion() {
        logger.debug { "Simulando" }
        var tiempo = 0
        var salida = false

        setTiempo(tiempo)
        state.tiempoActual.value = 0
        do {
            printOperacion("Tiempo: ${tiempo / 1000}s")
            setTiempo(tiempo / 1000)
            printOperacion("Escaneado y Apuntando")
            // Obtengo la posicion a disparar
            val fil = (0 until state.dimensionEspacio.value).random()
            val col = (0 until state.dimensionEspacio.value).random()
            printOperacion("Disparando en: [$fil][$col]")

            // Disparo
            actualizarDisparo()
            // Acierto si es != null
            if (espacio[fil, col] != null) {
                printOperacion("Has acertado en la posicion: [${fil + 1}] [${col + 1}]")

                var efecto: Int = getEfecto()

                printOperacion("La energia del droide es: ${espacio[fil, col]?.energia}\nEfecto de disparo: $efecto")

                val d = espacio[fil, col]!! // Sabemos que no es nulo!!!

                // Si el androide es del tipo SW447, tiene una defensa que lo delimita de los disparos
                if (d is DroideW447) {
                    printOperacion("El droide SW447 tiene una defensa que lo delimita de los disparos")
                    efecto -= d.defensa
                    efecto = if (efecto < 0) 0 else efecto

                    // Si implementa una interfaz de defensa
                } else if (d is Defensa) {
                    printOperacion("El droide implementa una interfaz de Escudo de Defensa")
                    efecto = (d as Defensa).defender(efecto)
                    efecto = if (efecto < 0) 0 else efecto
                }
                d.energia -= efecto
                printOperacion("Has acertado con un daño de: $efecto a: $d")
                actualizarAciertos()

                // Ha muerto
                if (!d.isAlive) {
                    printOperacion("El droide ha muerto  pues su energia es: ${d.energia}")
                    actualizarMuertos()
                } else {
                    printOperacion("El droide sigue vivo con una energia de: ${d.energia}")
                }

            } else {
                printOperacion("Has fallado en la posicion: [${fil + 1}] [${col + 1}]")
            }
            tiempo += 1000
            setTiempo(tiempo / 1000)
            // Pauso!! ---> Esto es lo que te va a dar problemas!!!!
            delay(1000)
            // Cambian de posicion los droides
            if (tiempo % 3000 == 0) {
                colocarDroidesEnemigos() // Cambiamos de posicion los droides
            }
            // Imprimo el espacio
            printCuadrante()
            val finTiempo: Boolean = tiempo >= state.tiempoTotal.value * 1000
            val todosMuertos = state.droidesTotal.value == state.muertos.value
            salida = finTiempo || todosMuertos
        } while (!salida)
        printOperacion("Fin de la simulacion")
    }

    private fun actualizarMuertos() {
        state.muertos.value = state.muertos.value + 1
    }

    private fun actualizarAciertos() {
        state.aciertos.value = state.aciertos.value + 1
    }

    private fun actualizarDisparo() {
        state.disparos.value = state.disparos.value + 1
    }

    private fun getEfecto(): Int {
        val efecto = (Math.random() * 100).toInt()
        return if (efecto <= 15) {
            efectoDisparoCritical
        } else {
            efectoDisparoNormal
        }
    }

    private fun setTiempo(tiempo: Int) {
        state.tiempoActual.value = tiempo
    }

    private fun printOperacion(menssage: String) {
        state.operacion.value = state.operacion.value + "\n$menssage"
        println(menssage)
    }

    private fun printCuadrante() {
        state.cuadrante.value = matrixToArea()
        println(espacio)
    }

    private fun estadoResultadoFinal() {
        state.resultado.value = Ok(StarWarsResultado.StarWarsFin("Simulación terminada"))
    }

    fun guardarInforme(file: File) {
        file.writeText(report())
    }

    private fun report(): String {
        val sb = StringBuilder()
        sb.append("Informe de la simulacion\n")
        sb.append("Tiempo total de la simulacion: ${state.tiempoTotal.value} s\n")
        sb.append("Dimensión de la cuadrícula: ${state.dimensionEspacio.value} x  ${state.dimensionEspacio.value} \n")
        sb.append("Droides enemigos totales: ${state.droidesTotal.value}\n")
        sb.append("Droides enemigos muertos: ${state.muertos.value}\n")
        sb.append("Droides enemigos vivos: ${state.droidesTotal.value - state.muertos.value}\n")
        sb.append("Disparos realizados: ${state.disparos.value}\n")
        sb.append("Aciertos: ${state.aciertos.value}\n")
        // Porcetaje de aciertos sobre disparos
        sb.append("Procentaje de aciertos: ${((state.aciertos.value * 100) / state.disparos.value.toDouble()).toLocalNumber()} %\n")
        // enemigos ordenados por energia
        sb.append("Enemigos por energia\n")
        sb.append("Enemigos ordenados por energia: ${enemigos.toList().sortedBy { it.energia }}\n")
        // enemigos ordenados por modelo
        sb.append("Enemigos por modelo\n")
        sb.append("Enemigos ordenados por modelo: ${enemigos.toList().sortedBy { it.model }}\n")
        // Enemigos vivos
        sb.append("Enemigos vivos: ${enemigos.toList().count { it.isAlive }}\n")
        // Enemigos muertos
        sb.append("Enemigos muertos: ${enemigos.toList().count { !it.isAlive }}\n")
        return sb.toString()
    }


    // Clase que representa el estado de la vista
    data class StarWarsState(
        val dimensionEspacio: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val droidesTotal: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val tiempoTotal: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val tiempoActual: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val operacion: SimpleStringProperty = SimpleStringProperty(""),
        val cuadrante: SimpleStringProperty = SimpleStringProperty(""),
        val disparos: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val aciertos: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val muertos: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val resultado: SimpleObjectProperty<Result<StarWarsResultado, StarWarsError>> = SimpleObjectProperty(
            Ok(StarWarsResultado.StarWarsLoading)
        )
    )
}
