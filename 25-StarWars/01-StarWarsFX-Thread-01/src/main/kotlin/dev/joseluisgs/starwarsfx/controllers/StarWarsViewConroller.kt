package dev.joseluisgs.starwarsfx.controllers

import com.github.michaelbull.result.onSuccess
import dev.joseluisgs.starwarsfx.routes.RoutesManager
import dev.joseluisgs.starwarsfx.viewmodel.StarWarsResultado
import dev.joseluisgs.starwarsfx.viewmodel.StarWarsViewModel
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.stage.FileChooser
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.concurrent.thread


private val logger = KotlinLogging.logger {}

class StarWarsViewConroller : KoinComponent {
    // Mi ViewModel, debemos inyectarlo bien :) o hacer el get() de Koin si no lo queremos lazy
    val viewModel: StarWarsViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuSalir: MenuItem

    @FXML
    private lateinit var menuAcercaDe: MenuItem

    @FXML
    private lateinit var menuInforme: MenuItem

    // Spinners
    @FXML
    private lateinit var spinnerDimension: Spinner<Int>

    @FXML
    private lateinit var spinnerTiempo: Spinner<Int>

    @FXML
    private lateinit var spinnerDroides: Spinner<Int>

    // Areas de texto
    @FXML
    private lateinit var textOperacion: TextArea

    @FXML
    private lateinit var textAciertos: TextField

    @FXML
    private lateinit var textDisparos: TextField

    @FXML
    private lateinit var textMuertos: TextField

    @FXML
    private lateinit var textCuadrante: TextArea

    @FXML
    private lateinit var textTiempo: TextField

    // Botones
    @FXML
    lateinit var botonComenzar: Button

    @FXML
    lateinit var progressBar: ProgressBar


    @FXML
    fun initialize() {
        logger.info { "Inicializando StarWars Controller FXML" }

        // Eventos de los menus
        menuSalir.setOnAction { onOnCloseAction() }
        menuAcercaDe.setOnAction { onAcercaDeAction() }
        menuInforme.setOnAction { onInformeAction() }

        // Eventos de los botones
        botonComenzar.setOnAction { onComenzarAction() }

        // Iniciamos los spinners
        spinnerDimension.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(5, 9, 5)
        spinnerDroides.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(5, 25, 10)
        spinnerTiempo.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(30, 200, 30)

        // Bindemos las areas de texto, unidir
        textOperacion.textProperty().bind(viewModel.state.operacion)
        textCuadrante.textProperty().bind(viewModel.state.cuadrante)
        textAciertos.textProperty().bind(viewModel.state.aciertos.asString())
        textDisparos.textProperty().bind(viewModel.state.disparos.asString())
        textMuertos.textProperty().bind(viewModel.state.muertos.asString())
        textTiempo.textProperty().bind(viewModel.state.tiempoActual.asString())

        // Bindemos las areas de texto, lo hago en este hilo para no tener que hacerlo en el hilo de JavaFX
        progressBar.progressProperty().bind(viewModel.state.tiempoActual.divide(spinnerTiempo.value.toDouble()))

        // Ahora cogemos el objeto que tiene result y lo escuchamos a ver si cambia!!!
        // Y solo si cambia al estado final, lo mostramos
        viewModel.state.resultado.addListener { _, _, newValue ->
            newValue.onSuccess {
                if (it is StarWarsResultado.StarWarsFin) {
                    onTerminadoAction()
                }
            }
        }
    }

    private fun onComenzarAction() {
        logger.info { "Comenzando simulación" }
        // Recogemos los valores de los spinners
        val dimension = spinnerDimension.value.toInt()
        val droides = spinnerDroides.value.toInt()
        val tiempo = spinnerTiempo.value.toInt()

        logger.info { "Dimension: $dimension, Droides: $droides, Tiempo: $tiempo" }

        botonComenzar.isDisable = true

        // No dejamos actualizar el texto
        println("Comenzando simulación con $dimension, $droides, $tiempo")
        botonComenzar.scene.cursor = Cursor.WAIT

        // Comenzamos la simulación, esto debe ir en un hilo, al ser una tarea pesada
        // lo hacemos en un hilo, para no bloquear el hilo de JavaFX
        thread { viewModel.comenzarSimulacion(dimension, tiempo, droides) }

    }

    private fun onTerminadoAction() {
        logger.info { "Terminando simulación" }
        // Mostramos un mensaje de que ha terminado
        // Le ponemos un runLater para que se ejecute en el hilo de JavaFX
        // y no en el hilo de la simulación, además así no bloqueamos el hilo
        // se hará cuando se pueda y haya terminado
        botonComenzar.scene.cursor = Cursor.DEFAULT
        botonComenzar.isDisable = false
        // viewModel.state.isTerminado.value = false
        Alert(AlertType.INFORMATION).apply {
            title = "Simulación terminada"
            headerText = "¡Simulación terminada!"
            contentText = "La simulación de la batalla ha terminado correctamente"
            showAndWait()
        }
        // Confirmamos para guardar el informe
        val confirm = Alert(AlertType.CONFIRMATION).apply {
            title = "Guardar informe"
            headerText = "Salvar simulación"
            contentText = "¿Desea guardar el informe de la simulación?"
            showAndWait()
        }
        if (confirm.result == ButtonType.OK) {
            onInformeAction()
        }

    }

    private fun onInformeAction() {
        logger.debug { "onInformeAction" }
        // Guardamos el informe
        FileChooser().apply {
            title = "Guardar informe"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Text Files", "*.txt"),
            )
            showSaveDialog(RoutesManager.activeStage)?.let {
                viewModel.guardarInforme(it)
            }
        }
    }

    private fun onAcercaDeAction() {
        RoutesManager.initAcercaDeStage()
    }


    // Método para salir de la aplicación
    fun onOnCloseAction() {
        logger.debug { "onOnCloseAction" }

        val confirm = Alert(AlertType.CONFIRMATION).apply {
            headerText = "Salir de App"
            title = "Salir de App"
            contentText = "¿Desea salir de Star Wars?"
            showAndWait()
        }

        if (confirm.result == ButtonType.OK) {
            Platform.exit() // O System.exit(0)
        } else {
            confirm.close()
        }
    }
}
