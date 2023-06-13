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
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
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
    val state = SimpleObjectProperty(ExpedienteState())

    init {
        logger.debug { "Inicializando ExpedientesViewModel" }
        loadAlumnosFromRepository() // Cargamos los datos del repositorio
        loadTypes() // Cargamos los tipos de repetidor
    }

    private fun loadTypes() {
        logger.debug { "Cargando tipos de repetidor" }
        state.value = state.value.copy(
            typesRepetidor = listOf(
                TipoFiltro.TODOS.value,
                TipoFiltro.SI.value,
                TipoFiltro.NO.value
            )
        )
    }

    private fun loadAlumnosFromRepository() {
        logger.debug { "Cargando alumnos del repositorio" }
        val lista = repository.findAll()
        logger.debug { "Cargando alumnos del repositorio: ${lista.size}" }
        updateState(lista)
    }

    // Actualiza el estado de la aplicación con los datos de ese instante en el estado
    private fun updateState(listaAlumnos: List<Alumno>) {
        logger.debug { "Actualizando estado de Aplicacion" }
        val numAprobados = listaAlumnos.count { it.isAprobado() }
        val notaMedia = listaAlumnos.map { it.calificacion }.average().round(2)
        val alumnoSeleccionado = AlumnoFormulario()

        state.value = state.value.copy(
            alumnos = listaAlumnos.sortedBy { it.apellidos }, // Ordenamos por apellidos
            numAprobados = numAprobados,
            notaMedia = notaMedia,
            alumnoSeleccionado = alumnoSeleccionado
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

    fun saveAlumnadoToJson(file: File): Result<Long, AlumnoError> {
        logger.debug { "Guardando Alumnado en JSON" }
        return storage.storeDataJson(file, state.value.alumnos)
    }

    fun loadAlumnadoFromJson(file: File, withImages: Boolean = false): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Cargando Alumnado en JSON" }
        // Borramos todas las imagenes e iniciamos el proceso
        return storage.deleteAllImages().andThen {
            storage.loadDataJson(file).onSuccess {
                repository.deleteAll() // Borramos todos los datos de la BD
                // Guardamos los nuevos, pero hay que quitarle el ID, porque trabajamos con el NEW!!
                repository.saveAll(
                    if (withImages)
                        it
                    else
                        it.map { a -> a.copy(id = Alumno.NEW_ALUMNO, imagen = TipoImagen.SIN_IMAGEN.value) }
                )
                loadAlumnosFromRepository() // Actualizamos la lista
            }
        }
    }

    // carga en el estado el alumno seleccionado
    fun updateAlumnoSeleccionado(alumno: Alumno) {
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

        state.value = state.value.copy(alumnoSeleccionado = alumnoSeleccionado)
    }


    // Crea un nuevo alumno en el estado y repositorio
    fun crearAlumno(alumnoNuevo: AlumnoFormulario): Result<Alumno, AlumnoError> {
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
                val new = repository.save(newAlumno)
                // Actualizamos la lista
                // Podriamos cargar del repositorio otra vez, si fuera concurente o
                // conectada a un servidor remoto debería hacerlo así
                updateState(state.value.alumnos + new)
                Ok(new)
            }
    }

    // Edita un alumno en el estado y repositorio
    fun editarAlumno(alumnoEditado: AlumnoFormulario): Result<Alumno, AlumnoError> {
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
                val updated = repository.save(updatedAlumno)
                // Actualizamos la lista
                // Podriamos cargar del repositorio otra vez, si fuera concurente o
                // conectada a un servidor remoto debería hacerlo así
                //val lista = state.value.alumnos.toMutableList()
                //val indexedValue = lista.indexOfFirst { a -> a.id == updated.id }
                //lista[indexedValue] = updated
                // updateState(lista)
                updateState(
                    state.value.alumnos.filter { it.id != updated.id } + updated
                )
                Ok(updated)
            }
    }

    // Elimina un alumno en el estado y repositorio
    fun eliminarAlumno(): Result<Unit, AlumnoError> {
        logger.debug { "Eliminando Alumno" }
        // Hay que eliminar su imagen, primero siempre una copia!!!
        val alumno = state.value.alumnoSeleccionado.copy()
        // Para evitar que cambien en la selección!!!

        alumno.fileImage?.let {
            if (it.name != TipoImagen.SIN_IMAGEN.value) {
                storage.deleteImage(it)
            }
        }

        // Borramos del repositorio
        repository.deleteById(alumno.numero)
        // Actualizamos la lista
        // Podriamos cargar del repositorio otra vez, si fuera concurente o
        // conectada a un servidor remoto debería hacerlo así
        updateState(state.value.alumnos.filter { it.id != alumno.numero })
        return Ok(Unit)
    }

    fun exportToZip(fileToZip: File): Result<Unit, AlumnoError> {
        logger.debug { "Exportando a ZIP: $fileToZip" }
        // recogemos los alumnos del repositorio
        val alumnos = repository.findAll()
        storage.exportToZip(fileToZip, alumnos)
        return Ok(Unit)
    }

    fun loadAlumnadoFromZip(fileToUnzip: File): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Importando de ZIP: $fileToUnzip" }
        // recogemos los alumnos del repositorio
        return storage.loadFromZip(fileToUnzip).onSuccess { it ->
            repository.deleteAll()
            repository.saveAll(it.map { a -> a.copy(id = Alumno.NEW_ALUMNO) })
            loadAlumnosFromRepository()
        }
    }

    fun setTipoOperacion(tipo: TipoOperacion) {
        logger.debug { "Cambiando tipo de operación: $tipo" }
        state.value = state.value.copy(tipoOperacion = tipo)
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
        val tipoOperacion: TipoOperacion = TipoOperacion.NUEVO // Tipo de operacion
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
        var fileImage: File? = null
    )
}

