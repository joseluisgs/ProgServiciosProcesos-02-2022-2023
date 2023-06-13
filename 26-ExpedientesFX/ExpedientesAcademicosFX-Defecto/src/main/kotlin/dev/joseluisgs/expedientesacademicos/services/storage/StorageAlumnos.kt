package dev.joseluisgs.expedientesacademicos.services.storage

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.models.Alumno
import java.io.File

interface StorageAlumnos {
    fun storeDataJson(file: File, data: List<Alumno>): Result<Long, AlumnoError>
    fun loadDataJson(file: File): Result<List<Alumno>, AlumnoError>
    fun saveImage(fileName: File): Result<File, AlumnoError>
    fun loadImage(fileName: String): Result<File, AlumnoError>
    fun deleteImage(fileName: File): Result<Unit, AlumnoError>
    fun deleteAllImages(): Result<Long, AlumnoError>
    fun updateImage(fileImage: File, newFileImage: File): Result<File, AlumnoError>
    fun exportToZip(fileToZip: File, data: List<Alumno>): Result<File, AlumnoError>
    fun loadFromZip(fileToUnzip: File): Result<List<Alumno>, AlumnoError>
}