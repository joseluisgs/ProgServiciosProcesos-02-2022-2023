package dev.joseluisgs.expedientesacademicos.controllers

import com.github.michaelbull.result.*
import dev.joseluisgs.expedientesacademicos.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.locale.toLocalNumber
import dev.joseluisgs.expedientesacademicos.models.Alumno
import dev.joseluisgs.expedientesacademicos.routes.RoutesManager
import dev.joseluisgs.expedientesacademicos.viewmodels.ExpedientesViewModel
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

class DetalleViewController : KoinComponent {
    // Inyectamos nuestro ViewModel
    val viewModel: ExpedientesViewModel by inject()

    // Botones
    @FXML
    private lateinit var btnGuardar: Button

    @FXML
    private lateinit var btnLimpiar: Button

    @FXML
    private lateinit var btnCancelar: Button

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

    private var imageFileAlumno: File? = null

    @FXML
    fun initialize() {
        // logger.debug { "Inicializando DetalleViewController FXML en Modo: ${viewModel.state.tipoOperacion}" }

        textAlumnoNumero.isEditable = false // No se puede editar el número

        // Iniciamos los bindings
        initBindings()

        // Iniciamos los eventos
        initEventos()
    }


    private fun initEventos() {
        // Botones
        btnGuardar.setOnAction {
            MainScope().launch(Dispatchers.Main) {
                onGuardarAction()
            }
        }
        btnLimpiar.setOnAction {
            onLimpiarAction()
        }
        btnCancelar.setOnAction {
            onCancelarAction()
        }

        imageAlumno.setOnMouseClicked {
            onImageAction()
        }
    }

    private fun onImageAction() {
        logger.debug { "onImageAction" }
        // Abrimos un diálogo para seleccionar una imagen, esta vez lo he hecho más compacto!!!
        // Comparalo con los de Json!!!
        FileChooser().run {
            title = "Selecciona una imagen"
            extensionFilters.addAll(FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let {
            imageAlumno.image = Image(it.toURI().toString())
            imageFileAlumno = it
        }

    }

    private fun initBindings() {
        // Si es el modo de edición, cargamos los datos del alumno
        if (viewModel.state.value.tipoOperacion == ExpedientesViewModel.TipoOperacion.EDITAR) {

            textAlumnoNumero.text =
                if (viewModel.state.value.alumnoSeleccionado.numero == Alumno.NEW_ALUMNO) "" else viewModel.state.value.alumnoSeleccionado.numero.toString()
            textAlumnoApellidos.text = viewModel.state.value.alumnoSeleccionado.apellidos
            textAlumnoNombre.text = viewModel.state.value.alumnoSeleccionado.nombre
            textAlumnoEmail.text = viewModel.state.value.alumnoSeleccionado.email
            dateAlumnoFechaNacimiento.value = viewModel.state.value.alumnoSeleccionado.fechaNacimiento
            textAlumnoCalificacion.text =
                if (viewModel.state.value.alumnoSeleccionado.calificacion == 0.0) "" else viewModel.state.value.alumnoSeleccionado.calificacion.toLocalNumber()
            checkAlumnoRepetidor.isSelected = viewModel.state.value.alumnoSeleccionado.repetidor
            imageAlumno.image = viewModel.state.value.alumnoSeleccionado.imagen
        }
    }

    private suspend fun onGuardarAction() {
        logger.debug { "onGuardarActio" }
        val scope = MainScope()
        // Dependiendo del modo
        when (viewModel.state.value.tipoOperacion) {

            ExpedientesViewModel.TipoOperacion.NUEVO -> {
                validateForm().andThen {
                    viewModel.crearAlumno(it.copy(numero = Alumno.NEW_ALUMNO))
                }.onSuccess { a ->
                    logger.debug { "Alumno creado correctamente" }
                    showAlertOperacion(
                        AlertType.INFORMATION,
                        title = "Alumno creado",
                        header = "Alumno creado almacenado correctamente",
                        mensaje = "Se ha salvado en el sistema de gestión:\n${a.nombreCompleto}"
                    )
                    cerrarVentana()
                }.onFailure { e ->
                    logger.error { "Error al salvar alumno/a: ${e.message}" }
                    showAlertOperacion(
                        AlertType.ERROR,
                        title = "Error al salvar alumno/a",
                        header = "Se ha producido un error al salvar el alumno/a",
                        mensaje = "Se ha producido un error al salvar en el sistema de gestión a:\n${e.message}"
                    )
                }
            }

            ExpedientesViewModel.TipoOperacion.EDITAR -> {
                validateForm().andThen {
                    viewModel.editarAlumno(it)
                }.onSuccess { a ->
                    logger.debug { "Alumno editado correctamente" }
                    showAlertOperacion(
                        AlertType.INFORMATION,
                        "Alumno editado",
                        header = "Alumno editado correctamente",
                        mensaje = "Se ha salvado en el sistema de gestión:\n${a.nombreCompleto}"
                    )
                    cerrarVentana()
                }.onFailure { e ->
                    logger.error { "Error al actualizar alumno/a: ${e.message}" }
                    showAlertOperacion(
                        AlertType.ERROR,
                        title = "Error al actualizar alumno/a",
                        header = "Se ha producido un error al actualizar el alumno/a",
                        mensaje = "Se ha producido un error al actualizar el sistema de gestión:\n${e.message}"
                    )
                }
            }
        }
    }

    private fun cerrarVentana() {
        // truco coger el stage asociado a un componente
        btnCancelar.scene.window.hide()
    }

    private fun onCancelarAction() {
        logger.debug { "onCancelarAction" }
        cerrarVentana()
    }

    private fun onLimpiarAction() {
        logger.debug { "onLimpiarAction" }
        limpiarForm()
    }

    private fun limpiarForm() {
        // Limpiamos el estado actual
        if (viewModel.state.value.tipoOperacion == ExpedientesViewModel.TipoOperacion.NUEVO) {
            // si es nuevo lo ponemos a "", pero si es editar no lo tocamos
            textAlumnoNumero.text = ""
        }
        textAlumnoApellidos.text = ""
        textAlumnoNombre.text = ""
        textAlumnoEmail.text = ""
        dateAlumnoFechaNacimiento.value = null
        textAlumnoCalificacion.text = ""
        checkAlumnoRepetidor.isSelected = false
        imageAlumno.image = viewModel.getDefautltImage()
    }

    // Lo puedo hacer aquí o en mi validador en el viewModel
    private fun validateForm(): Result<ExpedientesViewModel.AlumnoFormulario, AlumnoError> {
        logger.debug { "validateForm" }

        // Validacion del formulario
        if (textAlumnoApellidos.text.isNullOrEmpty()) {
            return Err(AlumnoError.ValidationProblem("Apellidos no puede estar vacío"))
        }
        if (textAlumnoNombre.text.isNullOrEmpty()) {
            return Err(AlumnoError.ValidationProblem("Nombre no puede estar vacío"))
        }
        // Validamos el email, expresión regular
        if (textAlumnoEmail.text.isNullOrEmpty() || !textAlumnoEmail.text.matches(Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+"))) {
            return Err(AlumnoError.ValidationProblem("Email no puede estar vacío o no tiene el formato correcto"))
        }
        if (dateAlumnoFechaNacimiento.value == null || dateAlumnoFechaNacimiento.value.isAfter(LocalDate.now())) {
            return Err(AlumnoError.ValidationProblem("Fecha de nacimiento no puede estar vacía y debe ser anterior a hoy"))
        }
        if (textAlumnoCalificacion.text.isNullOrEmpty() || textAlumnoCalificacion.text.replace(",", ".")
                .toDoubleOrNull() == null || textAlumnoCalificacion.text.replace(",", ".")
                .toDouble() < 0 || textAlumnoCalificacion.text.replace(",", ".").toDouble() > 10
        ) {
            return Err(AlumnoError.ValidationProblem("Calificación no puede estar vacía y debe ser un número entre 0 y 10"))
        }
        return Ok(
            ExpedientesViewModel.AlumnoFormulario(
                numero = if (textAlumnoNumero.text.isNullOrEmpty()) Alumno.NEW_ALUMNO else textAlumnoNumero.text.toLong(),
                apellidos = textAlumnoApellidos.text,
                nombre = textAlumnoNombre.text,
                email = textAlumnoEmail.text,
                fechaNacimiento = dateAlumnoFechaNacimiento.value,
                calificacion = textAlumnoCalificacion.text.replace(",", ".").toDouble(),
                repetidor = checkAlumnoRepetidor.isSelected,
                imagen = imageAlumno.image,
                fileImage = imageFileAlumno
            )
        )
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


}



