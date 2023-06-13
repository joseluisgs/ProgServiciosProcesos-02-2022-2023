package dev.joseluisgs.expedientesacademicos.routes

import dev.joseluisgs.expedientesacademicos.controllers.ExpedientesViewConroller
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import mu.KotlinLogging
import java.io.InputStream
import java.net.URL
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Clase que gestiona las rutas de la aplicación
 */
object RoutesManager {
    // Necesitamos siempre saber
    private lateinit var mainStage: Stage // La ventana principal
    private lateinit var _activeStage: Stage // La ventana actual
    val activeStage: Stage
        get() = _activeStage
    lateinit var app: Application // La aplicación

    // Podemos tener una cache de escenas cargadas
    private var scenesMap: HashMap<String, Pane> = HashMap()

    // Definimos las rutas de las vistas que tengamos
    enum class View(val fxml: String) {
        MAIN("views/expedientes-view.fxml"),
        ACERCA_DE("views/acerca-de-view.fxml"),
        DETAILS("views/detalle-view.fxml"),
    }


    init {
        logger.debug { "Inicializando RoutesManager" }
        // Podemos configurar el idioma de la aplicación aquí
        Locale.setDefault(Locale("es", "ES"))
    }

    // Recuerda
    // Si cambiamos de ventana, cambiamos una stage y una scena
    // Si mantenemos la ventana, cambiamos solo la scena

    // Inicializamos la scena principal
    fun initMainStage(stage: Stage) {
        logger.debug { "Inicializando MainStage" }

        val fxmlLoader = FXMLLoader(getResource(View.MAIN.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val scene = Scene(parentRoot, 680.0, 440.0)
        // scene.stylesheets.add(getResource(Style.DAM.css).toExternalForm())
        stage.title = "Pokedex"
        stage.isResizable = false
        stage.icons.add(Image(getResourceAsStream("icons/app-icon.png")))
        stage.setOnCloseRequest {
            // Podemos hacer algo antes de cerrar
            // println("Cerrando la ventana")
            val controller = fxmlLoader.getController<ExpedientesViewConroller>()
            controller.onOnCloseAction()
        }
        stage.scene = scene
        mainStage = stage
        _activeStage = stage
        mainStage.show()

    }

    // Repetimos por cada stage configurando el stage y la scena
    //....

    // Abrimos one como una nueva ventana
    fun initAcercaDeStage() {
        logger.debug { "Inicializando AcercaDeStage" }

        val fxmlLoader = FXMLLoader(getResource(View.ACERCA_DE.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val scene = Scene(parentRoot, 395.0, 155.0)
        val stage = Stage()
        stage.title = "Acerca De Pokedex"
        stage.scene = scene
        stage.initOwner(mainStage)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.isResizable = false

        stage.show()
    }

    // Abrimos one como una nueva ventana
    fun initDetalle() {
        logger.debug { "Inicializando Detalle" }

        val fxmlLoader = FXMLLoader(getResource(View.DETAILS.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val scene = Scene(parentRoot, 350.0, 400.0)
        val stage = Stage()
        stage.title = "Detalle de Alumno/a"
        stage.scene = scene
        stage.initOwner(mainStage)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.isResizable = false

        stage.show()
    }

    // O podemos hacer uno genérico, añade las opciones que necesites
    fun getResource(resource: String): URL {
        return app::class.java.getResource(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso: $resource")
    }

    fun getResourceAsStream(resource: String): InputStream {
        return app::class.java.getResourceAsStream(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso como stream: $resource")
    }
}

