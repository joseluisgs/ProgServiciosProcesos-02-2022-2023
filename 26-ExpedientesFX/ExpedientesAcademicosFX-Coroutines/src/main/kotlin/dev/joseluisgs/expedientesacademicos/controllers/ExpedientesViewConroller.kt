package dev.joseluisgs.expedientesacademicos.controllers

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.joseluisgs.expedientesacademicos.locale.toLocalNumber
import dev.joseluisgs.expedientesacademicos.models.Alumno
import dev.joseluisgs.expedientesacademicos.routes.RoutesManager
import dev.joseluisgs.expedientesacademicos.viewmodels.ExpedientesViewModel
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.Cursor.DEFAULT
import javafx.scene.Cursor.WAIT
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


private val logger = KotlinLogging.logger {}

class ExpedientesViewConroller : KoinComponent {
    // Inyectamos nuestro ViewModel
    val viewModel: ExpedientesViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuImportar: MenuItem

    @FXML
    private lateinit var menuExportar: MenuItem

    @FXML
    private lateinit var menuZip: MenuItem

    @FXML
    private lateinit var menuUnzip: MenuItem

    @FXML
    private lateinit var menuSalir: MenuItem

    @FXML
    private lateinit var menuAcercaDe: MenuItem

    // Botones
    @FXML
    private lateinit var btnNuevo: Button

    @FXML
    private lateinit var btnEditar: Button

    @FXML
    private lateinit var btnEliminar: Button

    //Combo
    @FXML
    private lateinit var comboTipo: ComboBox<String>

    // Tabla
    @FXML
    private lateinit var tableAlumnos: TableView<Alumno>

    @FXML
    private lateinit var tableColumnNumero: TableColumn<Alumno, Long>

    @FXML
    private lateinit var tableColumNombre: TableColumn<Alumno, String>

    @FXML
    private lateinit var tableColumnCalificacion: TableColumn<Alumno, Double>

    // Buscador
    @FXML
    private lateinit var textBuscador: TextField

    // Estadisticas
    @FXML
    private lateinit var textNumAprobados: TextField

    @FXML
    private lateinit var textNotaMedia: TextField

    // Formulario del alumno
    @FXML
    private lateinit var textAlumnoNumero: TextField

    @FXML
    private lateinit var textAlumnoApellidos: TextField

    @FXML
    private lateinit var textAlumnoNombre: TextField

    @FXML
    private lateinit var textAlumnoEmail: TextField

    @FXML
    private lateinit var dateAlumnoFechaNacimiento: DatePicker

    @FXML
    private lateinit var textAlumnoCalificacion: TextField

    @FXML
    private lateinit var checkAlumnoRepetidor: CheckBox

    @FXML
    private lateinit var imageAlumno: ImageView

    // Metodo para inicializar
    @FXML
    fun initialize() {
        logger.debug { "Inicializando ExpedientesDeViewController FXML" }

        // Iniciamos los bindings
        initBindings()

        // Iniciamos los eventos
        initEventos()
    }

    private fun initBindings() {
        logger.debug { "Inicializando bindings" }

        // comboBoxe
        comboTipo.items = FXCollections.observableArrayList(viewModel.state.value.typesRepetidor)
        comboTipo.selectionModel.selectFirst()

        // Tablas
        tableAlumnos.items = FXCollections.observableArrayList(viewModel.state.value.alumnos)
        tableAlumnos.selectionModel.selectionMode = SelectionMode.SINGLE

        // columnas, con el nombre de la propiedad del objeto hará binding
        tableColumnNumero.cellValueFactory = PropertyValueFactory("id")
        tableColumNombre.cellValueFactory = PropertyValueFactory("nombreCompleto")
        tableColumnCalificacion.cellValueFactory = PropertyValueFactory("calificacion")

        textNumAprobados.text = viewModel.state.value.numAprobados.toString()
        textNotaMedia.text = viewModel.state.value.notaMedia.toString()

        MainScope().launch(Dispatchers.Main) {
            viewModel.state.collect { newState ->
                updatesEstadisticas(newState)
                updatesFormulario(newState)
                // poque es una operación costosa
                updatesTabla(newState)
            }
        }

        // Para que no se vea desactivado mucho, que queda feo!!
        val styleOpacity = "-fx-opacity: 1"
        dateAlumnoFechaNacimiento.style = styleOpacity
        dateAlumnoFechaNacimiento.editor.style = styleOpacity
        checkAlumnoRepetidor.style = styleOpacity
    }

    private fun updatesTabla(
        newState: ExpedientesViewModel.ExpedienteState,
    ) {
        // Solo si ha cambiado la lista de alumnos, limpiamos la selección
        /*if (newState.alumnos != tableAlumnos.items) {
            tableAlumnos.selectionModel.clearSelection()
            tableAlumnos.items = FXCollections.observableArrayList(newState.alumnos)
        }
*/
        val actual = viewModel.alumnosFilteredList(comboTipo.value, textBuscador.text.trim())
        if (actual != tableAlumnos.items) {
            // tableAlumnos.selectionModel.clearSelection()
            tableAlumnos.items = FXCollections.observableArrayList(actual)
        }
    }

    private fun updatesFormulario(
        newState: ExpedientesViewModel.ExpedienteState
    ) {
        // Solo si ha cambiado el alumno seleccionado
        if (textAlumnoApellidos.text != newState.alumnoSeleccionado.apellidos) {

            textAlumnoNumero.text =
                if (newState.alumnoSeleccionado.numero == Alumno.NEW_ALUMNO) "" else newState.alumnoSeleccionado.numero.toString()
            textAlumnoApellidos.text = newState.alumnoSeleccionado.apellidos
            textAlumnoNombre.text = newState.alumnoSeleccionado.nombre
            textAlumnoEmail.text = newState.alumnoSeleccionado.email
            dateAlumnoFechaNacimiento.value = newState.alumnoSeleccionado.fechaNacimiento
            textAlumnoCalificacion.text =
                if (newState.alumnoSeleccionado.calificacion == 0.0) "" else newState.alumnoSeleccionado.calificacion.toLocalNumber()
            checkAlumnoRepetidor.isSelected = newState.alumnoSeleccionado.repetidor
            imageAlumno.image = newState.alumnoSeleccionado.imagen
        }
    }

    private fun updatesEstadisticas(
        newState: ExpedientesViewModel.ExpedienteState
    ) {
        // Solo si ha cambiado
        if (newState.numAprobados != textNumAprobados.text.toIntOrNull()) {
            textNumAprobados.text = newState.numAprobados.toString()
        }
        // Solo si ha cambiado
        if (newState.notaMedia != textNotaMedia.text.toDoubleOrNull()) {
            textNotaMedia.text = newState.notaMedia.toString()
        }
    }


    private fun initEventos() {
        logger.debug { "Inicializando eventos" }

        // menús
        menuImportar.setOnAction {
            MainScope().launch(Dispatchers.Main) {
                onImportarAction()
            }
        }

        menuExportar.setOnAction {
            MainScope().launch(Dispatchers.Main) {
                onExportarAction()
            }
        }

        menuZip.setOnAction {
            MainScope().launch(Dispatchers.Main) {
                onZipAction()
            }
        }

        menuUnzip.setOnAction {
            MainScope().launch(Dispatchers.Main) {
                onUnzipAction()
            }
        }

        menuSalir.setOnAction {
            onOnCloseAction()
        }

        menuAcercaDe.setOnAction {
            onAcercaDeAction()
        }

        // Botones
        btnNuevo.setOnAction {
            onNuevoAction()
        }

        btnEditar.setOnAction {
            onEditarAction()
        }

        btnEliminar.setOnAction {
            MainScope().launch(Dispatchers.Main) {
                onEliminarAction()
            }
        }

        // Combo
        comboTipo.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { onComboSelected(it) }
        }

        // Tabla
        tableAlumnos.selectionModel.selectedItemProperty().addListener { _, oldValue, newValue ->
            newValue?.let {
                if (oldValue != newValue) {
                    MainScope().launch(Dispatchers.Main) {
                        onTablaSelected(newValue)
                    }
                }
            }
        }

        // Buscador
        // Evento del buscador key press
        // Funciona con el intro
        // textBuscador.setOnAction {
        // con cualquer letra al levantarse, ya ha cambiado el valor
        textBuscador.setOnKeyReleased { newValue ->
            newValue?.let { onKeyReleasedAction() }
        }
    }

    private fun onKeyReleasedAction() {
        logger.debug { "onKeyReleasedAction" }
        filterDataTable()
    }

    private fun filterDataTable() {
        logger.debug { "filterDataTable" }
        // filtramos por el tipo seleccionado en la tabla
        tableAlumnos.items = FXCollections.observableList(
            viewModel.alumnosFilteredList(comboTipo.value, textBuscador.text.trim())
        )
    }

    private suspend fun onTablaSelected(newValue: Alumno) {
        logger.debug { "onTablaSelected: $newValue" }
        viewModel.updateAlumnoSeleccionado(newValue)
    }

    private fun onComboSelected(newValue: String) {
        logger.debug { "onComboSelected: $newValue" }
        filterDataTable()
    }

    private suspend fun onEliminarAction() {
        logger.debug { "onEliminarAction" }
        // Comprbar que se ha seleccionado antes!!
        if (tableAlumnos.selectionModel.selectedItem == null) {
            return
        }
        val button = Alert(AlertType.CONFIRMATION).apply {
            title = "Eliminar Alumno"
            headerText = "¿Desea eliminar este alumno?"
            contentText = "Esta acción no se puede deshacer y se eliminarán todos los datos asociados al alumno."
        }.showAndWait().orElse(ButtonType.CANCEL)

        if (button == ButtonType.OK) {
            viewModel.eliminarAlumno().onSuccess {
                logger.debug { "Alumno/a eliminado correctamente" }
                showAlertOperacion(
                    alerta = AlertType.INFORMATION,
                    title = "Alumno/a eliminado/a",
                    header = "Alumno/a eliminado/a del sistema",
                    mensaje = "Se ha eliminado el/la alumno/a correctamente del sistema de gestión."
                )
            }.onFailure {
                logger.error { "Error al eliminar el alumno: ${it.message}" }
                showAlertOperacion(alerta = AlertType.ERROR, "Error al eliminar el/la alumno/a", it.message)
            }
        }

    }

    private fun onEditarAction() {
        logger.debug { "onEditarAction" }
        if (tableAlumnos.selectionModel.selectedItem == null) {
            return
        }
        viewModel.setTipoOperacion(ExpedientesViewModel.TipoOperacion.EDITAR)
        RoutesManager.initDetalle()
    }

    private fun onNuevoAction() {
        logger.debug { "onNuevoAction" }
        // Poner el modo nuevo antes!!
        viewModel.setTipoOperacion(ExpedientesViewModel.TipoOperacion.NUEVO)
        RoutesManager.initDetalle()
    }

    private fun onAcercaDeAction() {
        logger.debug { "onAcercaDeAction" }
        RoutesManager.initAcercaDeStage()
    }

    private suspend fun onExportarAction() {
        logger.debug { "onExportarAction" }
        // Forma larga, muy Java
        //val fileChooser = FileChooser()
        //fileChooser.title = "Exportar expedientes"
        //fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
        //val file = fileChooser.showSaveDialog(RoutesManager.activeStage)

        // Forma Kotlin con run y let (scope functions)
        FileChooser().run {
            title = "Exportar expedientes"
            extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
            showSaveDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onSaveAction: $it" }
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.saveAlumnadoToJson(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos exportados",
                        header = "Datos exportados correctamente a fichero JSON",
                        mensaje = "Se ha exportado tus Expedientes desde el fichero de gestión.\nAlumnos exportados: ${viewModel.state.value.alumnos.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al exportar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
    }

    private suspend fun onImportarAction() {
        logger.debug { "onImportarAction" }
        // Forma larga, muy Java
        //val fileChooser = FileChooser()
        //fileChooser.title = "Importar expedientes"
        //fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
        //val file = fileChooser.showOpenDialog(RoutesManager.activeStage)

        // Forma Kotlin con run y let (scope functions)
        FileChooser().run {
            title = "Importar expedientes"
            extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onAbrirAction: $it" }
            showAlertOperacion(
                AlertType.INFORMATION,
                title = "Importando datos",
                header = "Importando datos desde JSON",
                mensaje = "Importando datos Se sustituye la imagen por una imagen por defecto."
            )
            // Cambiar el cursor a espera
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.loadAlumnadoFromJson(it)
                .onSuccess { num ->
                    showAlertOperacion(
                        title = "Datos importados",
                        header = "Datos importados correctamente desde JSON",
                        mensaje = "Se ha importado tus Expedientes al sistema de gestión.\nAlumnos importados: $num"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al importar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
    }

    // Método para salir de la aplicación
    fun onOnCloseAction() {
        logger.debug { "onOnCloseAction" }

        Alert(AlertType.CONFIRMATION).apply {
            title = "Salir"
            headerText = "Salir de Expedientes DAM"
            contentText = "¿Desea salir de Expedientes DAM?"
        }.showAndWait().ifPresent { buttonType ->
            if (buttonType == ButtonType.OK) {
                Platform.exit()
            }
        }
    }

    private fun showAlertOperacion(
        alerta: AlertType = AlertType.CONFIRMATION,
        title: String = "",
        header: String = "",
        mensaje: String = ""
    ) {
        Alert(alerta).apply {
            this.title = title
            this.headerText = header
            this.contentText = mensaje
        }.showAndWait()
    }

    private suspend fun onUnzipAction() {
        logger.debug { "onUnzipAction" }
        FileChooser().run {
            title = "Importar desde Zip"
            extensionFilters.add(FileChooser.ExtensionFilter("ZIP", "*.zip"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let { it ->
            logger.debug { "onAbrirAction: $it" }
            showAlertOperacion(
                AlertType.INFORMATION, title = "Importando datos",
                header = "Importando datos desde un fichero ZIP",
                mensaje = "Se ha importado correctamente los datos. Se sustituye la imagen por una imagen por defecto."
            )
            // Cambiar el cursor a espera
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.loadAlumnadoFromZip(it)
                .onSuccess { num ->
                    showAlertOperacion(
                        title = "Datos importados desde Zip",
                        header = "Importando datos desde un fichero ZIP con éxito",
                        mensaje = "Se ha importado tus Expedientes.\nAlumnos importados: $num"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al importar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }

    }

    private suspend fun onZipAction() {
        logger.debug { "onZipAction" }
        FileChooser().run {
            title = "Exportar a Zip"
            extensionFilters.add(FileChooser.ExtensionFilter("ZIP", "*.zip"))
            showSaveDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onAbrirAction: $it" }
            // Cambiar el cursor a espera
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.exportToZip(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos exportados a Zip",
                        header = "Exportando datos a un fichero ZIP con éxito",
                        mensaje = "Se ha exportado tus Expedientes completos con imágenes a un fichero Zip.\nAlumnos exportados: ${viewModel.state.value.alumnos.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al exportar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
    }

}
