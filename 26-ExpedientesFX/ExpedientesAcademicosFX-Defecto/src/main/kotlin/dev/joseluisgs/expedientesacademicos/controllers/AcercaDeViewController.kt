package dev.joseluisgs.expedientesacademicos.controllers

import com.vaadin.open.Open
import javafx.fxml.FXML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class AcercaDeViewController {
    @FXML
    private lateinit var linkGitHub: javafx.scene.control.Hyperlink

    // Inicializamos
    @FXML
    fun initialize() {
        logger.info { "Inicializando AcercaDeViewController FXML" }
        linkGitHub.setOnAction {
            val url = "https://github.com/joseluisgs"
            logger.debug { "Abriendo navegador en el link: $url" }
            Open.open(url)
        }
    }
}
