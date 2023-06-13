package dev.joseluisgs.expedientesacademicos.viewmodels

import com.github.michaelbull.result.*
import dev.joseluisgs.expedientesacademicos.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.locale.round
import dev.joseluisgs.expedientesacademicos.mappers.toModel
import dev.joseluisgs.expedientesacademicos.models.Alumno
import dev.joseluisgs.expedientesacademicos.repositories.AlumnosRepository
import dev.joseluisgs.expedientesacademicos.routes.RoutesManager
import dev.joseluisgs.expedientesacademicos.services.storage.StorageAlumnos
import dev.joseluisgs.expedientesacademicos.validators.validate
import javafx.scene.image.Image
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.io.File
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

/*
Lo normal es hacer un ViewModel por cada vista, pero en este caso, como es una aplicación pequeña
y no tiene muchas vistas, lo haremos todo en uno.
Pero como ves la complejidad asciende y hay que tener muy claro lo que se está haciendo.
*/

private const val SIN_IMAGEN = "images/sin-imagen.png"

class ExpedientesViewModel(
    private val repository: AlumnosRepository,
    private val storage: StorageAlumnos
) {

    // Estado del ViewModel

    private val _state = MutableStateFlow(ExpedienteState())
    val state: StateFlow<ExpedienteState> = _state

    init {
        logger.debug { "Inicializando ExpedientesViewModel" }
        loadAlumnosFromRepository() // Cargamos los datos del repositorio
        loadTypes() // Cargamos los tipos de repetidor
    }

    private fun loadTypes() {
        logger.debug { "Cargando tipos de repetidor" }
        _state.value = state.value.copy(
            typesRepetidor = listOf(
                TipoFiltro.TODOS.value,
                TipoFiltro.SI.value,
                TipoFiltro.NO.value
            )
        )
    }

    private fun loadAlumnosFromRepository() {
        logger.debug { "Cargando alumnos del repositorio" }
        // Cargamos los alumnos del repositorio y nos suscribimos a los cambios
        // Al ser un hilo, pues nos quedamos en segundo plano
        // Ademas usamos sqldelight, que nos devuelve un Flow, que es un observable y detectamos cada cambio
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch(CoroutineName("LoadAlumnosFromRepository")) {
            repository.findAll().collect { lista ->
                logger.debug { "Cargando alumnos del repositorio: ${lista.size}" }
                updateState(lista)
            }
        }
    }

    // Actualiza el estado de la aplicación con los datos de ese instante en el estado
    private fun updateState(listaAlumnos: List<Alumno>) {
        logger.debug { "Actualizando estado de Aplicacion" }
        val numAprobados = listaAlumnos.count { it.isAprobado() }
        // Nota media if not NaN
        val notaMedia = listaAlumnos.map { it.calificacion }.average().takeIf { !it.isNaN() }?.round(2) ?: 0.0
        //val alumnoSeleccionado = AlumnoFormulario()

        _state.value = state.value.copy(
            alumnos = listaAlumnos.sortedBy { it.apellidos }, // Ordenamos por apellidos
            numAprobados = numAprobados,
            notaMedia = notaMedia,
            //alumnoSeleccionado = alumnoSeleccionado
        )
    }

    // Filtra la lista de alumnnos en el estado en función del tipo y el nombre completo
    fun alumnosFilteredList(tipo: String, nombreCompleto: String): List<Alumno> {
        logger.debug { "Filtrando lista de Alumnos: $tipo, $nombreCompleto" }

        return state.value.alumnos
            .filter { alumno ->
                when (tipo) {
                    TipoFiltro.SI.value -> alumno.repetidor
                    TipoFiltro.NO.value -> !alumno.repetidor
                    else -> true
                }
            }.filter { alumno ->
                alumno.nombreCompleto.contains(nombreCompleto, true)
            }

    }

    suspend fun saveAlumnadoToJson(file: File): Result<Long, AlumnoError> {
        logger.debug { "Guardando Alumnado en JSON" }
        return storage.storeDataJson(file, state.value.alumnos)
    }

    suspend fun loadAlumnadoFromJson(file: File, withImages: Boolean = false): Result<Long, AlumnoError> {
        logger.debug { "Cargando Alumnado en JSON" }
        // Borramos todas las imagenes e iniciamos el proceso

        storage.deleteAllImages().andThen {
            storage.loadDataJson(file).onSuccess { lista ->
                repository.deleteAll() // Borramos todos los datos de la BD
                // Guardamos los nuevos, pero hay que quitarle el ID, porque trabajamos con el NEW!!
                repository.saveAll(
                    if (withImages)
                        lista
                    else
                        lista.map { a -> a.copy(id = Alumno.NEW_ALUMNO, imagen = TipoImagen.SIN_IMAGEN.value) }
                )
                return Ok(lista.size.toLong())
            }
        }
        return Err(AlumnoError.LoadJson("Error al importar el JSON"))

    }

    // carga en el estado el alumno seleccionado
    suspend fun updateAlumnoSeleccionado(alumno: Alumno) {
        logger.debug { "Actualizando estado de Alumno: $alumno" }

        lateinit var fileImage: File
        lateinit var imagen: Image

        storage.loadImage(alumno.imagen).onSuccess {
            imagen = Image(it.absoluteFile.toURI().toString())
            fileImage = it
        }.onFailure {
            imagen = Image(RoutesManager.getResourceAsStream(SIN_IMAGEN))
            fileImage = File(RoutesManager.getResource(SIN_IMAGEN).toURI())
        }

        val alumnoSeleccionado = AlumnoFormulario(
            numero = alumno.id,
            apellidos = alumno.apellidos,
            nombre = alumno.nombre,
            email = alumno.email,
            fechaNacimiento = alumno.fechaNacimiento,
            calificacion = alumno.calificacion,
            repetidor = alumno.repetidor,
            fileImage = fileImage,
            imagen = imagen
        )

        _state.value = state.value.copy(alumnoSeleccionado = alumnoSeleccionado)
    }


    // Crea un nuevo alumno en el estado y repositorio
    suspend fun crearAlumno(alumnoNuevo: AlumnoFormulario): Result<Alumno, AlumnoError> {
        logger.debug { "Creando Alumno" }
        // creamos el alumno
        println("Alumno a crear: $alumnoNuevo")
        var newAlumno = alumnoNuevo.toModel().copy(id = Alumno.NEW_ALUMNO)
        return newAlumno.validate()
            .andThen {
                // Copiamos la imagen si no es nula
                println("Imagen a copiar: ${alumnoNuevo.fileImage}")
                alumnoNuevo.fileImage?.let { newFileImage ->
                    storage.saveImage(newFileImage).onSuccess {
                        println("Imagen copiada: ${it.name}")
                        newAlumno = newAlumno.copy(imagen = it.name)
                    }
                }
                Ok(repository.save(newAlumno))
            }
    }

    // Edita un alumno en el estado y repositorio
    suspend fun editarAlumno(alumnoEditado: AlumnoFormulario): Result<Alumno, AlumnoError> {
        logger.debug { "Editando Alumno" }
        // creamos el alumno
        val fileImageTemp = state.value.alumnoSeleccionado.fileImage // Nombre de la imagen que tiene
        var updatedAlumno = alumnoEditado.toModel().copy(imagen = fileImageTemp!!.name)
        return updatedAlumno.validate()
            .andThen {
                // Tenemos dos opciones, que no tuviese imagen o que si la tuviese
                alumnoEditado.fileImage?.let { newFileImage ->
                    if (updatedAlumno.imagen == TipoImagen.SIN_IMAGEN.value || updatedAlumno.imagen == TipoImagen.EMPTY.value) {
                        storage.saveImage(newFileImage).onSuccess {
                            updatedAlumno = updatedAlumno.copy(imagen = it.name)
                        }
                    } else {
                        storage.updateImage(fileImageTemp, newFileImage)
                    }
                }
                Ok(repository.save(updatedAlumno))
            }
    }

    // Elimina un alumno en el estado y repositorio
    suspend fun eliminarAlumno(): Result<Unit, AlumnoError> {
        logger.debug { "Eliminando Alumno" }
        // Hay que eliminar su imagen, primero siempre una copia!!!
        val alumno = state.value.alumnoSeleccionado.copy()
        // Para evitar que cambien en la selección!!!

        alumno.fileImage?.let {
            if (it.name != TipoImagen.SIN_IMAGEN.value) {
                storage.deleteImage(it)
            }
        }
        // Eliminamos el alumno
        return Ok(repository.deleteById(alumno.numero))
    }

    suspend fun exportToZip(fileToZip: File): Result<Unit, AlumnoError> {
        logger.debug { "Exportando a ZIP: $fileToZip" }
        // recogemos los alumnos del repositorio
        val alumnos = repository.findAll().first()
        storage.exportToZip(fileToZip, alumnos)
        return Ok(Unit)
    }

    suspend fun loadAlumnadoFromZip(fileToUnzip: File): Result<Long, AlumnoError> {
        logger.debug { "Importando de ZIP: $fileToUnzip" }
        // recogemos los alumnos del repositorio
        storage.loadFromZip(fileToUnzip).onSuccess { lista ->
            repository.deleteAll()
            repository.saveAll(lista.map { a -> a.copy(id = Alumno.NEW_ALUMNO) })
            return Ok(lista.size.toLong())
        }
        return Err(AlumnoError.ImportZip("Error al importar el ZIP"))
    }

    fun setTipoOperacion(tipo: TipoOperacion) {
        logger.debug { "Cambiando tipo de operación: $tipo" }
        _state.value = state.value.copy(tipoOperacion = tipo)
    }

    fun getDefautltImage(): Image {
        return Image(RoutesManager.getResourceAsStream(SIN_IMAGEN))
    }

    // Mi estado
    // Enums
    enum class TipoFiltro(val value: String) {
        TODOS("Todos/as"), SI("Repetidor/a"), NO("No Repetidor/a")
    }

    enum class TipoOperacion(val value: String) {
        NUEVO("Nuevo"), EDITAR("Editar")
    }

    enum class TipoImagen(val value: String) {
        SIN_IMAGEN("sin-imagen.png"), EMPTY("")
    }

    // Clases que representan el estado
    // Estado del ViewModel y caso de uso de Gestión de Expedientes
    data class ExpedienteState(
        // Los contenedores de colecciones deben ser ObservableList
        val typesRepetidor: List<String> = listOf(),
        val alumnos: List<Alumno> = listOf(),

        // Para las estadisticas
        val numAprobados: Int = 0,
        val notaMedia: Double = 0.0,

        // siempre que cambia el tipo de operacion, se actualiza el alumno
        val alumnoSeleccionado: AlumnoFormulario = AlumnoFormulario(), // Alumno seleccionado en tabla
        val tipoOperacion: TipoOperacion = TipoOperacion.NUEVO, // Tipo de operacion
    )

    // Estado para formularios de Alumno (seleccionado y de operaciones)
    data class AlumnoFormulario(
        val numero: Long = Alumno.NEW_ALUMNO,
        val apellidos: String = "",
        val nombre: String = "",
        val email: String = "",
        val fechaNacimiento: LocalDate = LocalDate.now(),
        val calificacion: Double = 0.0,
        val repetidor: Boolean = false,
        val imagen: Image = Image(RoutesManager.getResourceAsStream(SIN_IMAGEN)),
        val fileImage: File? = null
    )
}

