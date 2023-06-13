package dev.joseluisgs.expedientesacademicos.services.storage

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.models.Alumno
import java.io.File

interface StorageAlumnos {
    suspend fun storeDataJson(file: File, data: List<Alumno>): Result<Long, AlumnoError>
    suspend fun loadDataJson(file: File): Result<List<Alumno>, AlumnoError>
    suspend fun saveImage(fileName: File): Result<File, AlumnoError>
    suspend fun loadImage(fileName: String): Result<File, AlumnoError>
    suspend fun deleteImage(fileName: File): Result<Unit, AlumnoError>
    suspend fun deleteAllImages(): Result<Long, AlumnoError>
    suspend fun updateImage(fileImage: File, newFileImage: File): Result<File, AlumnoError>
    suspend fun exportToZip(fileToZip: File, data: List<Alumno>): Result<File, AlumnoError>
    suspend fun loadFromZip(fileToUnzip: File): Result<List<Alumno>, AlumnoError>
}