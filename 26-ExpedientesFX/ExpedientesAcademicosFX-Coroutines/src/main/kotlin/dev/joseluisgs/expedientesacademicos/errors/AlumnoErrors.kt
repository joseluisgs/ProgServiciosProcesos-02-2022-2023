package dev.joseluisgs.expedientesacademicos.errors

sealed class AlumnoError(val message: String) {
    class LoadJson(message: String) : AlumnoError(message)
    class SaveJson(message: String) : AlumnoError(message)
    class LoadImage(message: String) : AlumnoError(message)
    class SaveImage(message: String) : AlumnoError(message)
    class DeleteImage(message: String) : AlumnoError(message)
    class DeleteById(message: String) : AlumnoError(message)
    class ValidationProblem(message: String) : AlumnoError(message)
    class NotFound(message: String) : AlumnoError(message)
    class ExportZip(message: String) : AlumnoError(message)
    class ImportZip(message: String) : AlumnoError(message)
}