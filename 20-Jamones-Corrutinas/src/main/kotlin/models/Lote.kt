package models

import kotlinx.serialization.Serializable
import serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class Lote(
    // @Serializable(with = UUIDSerializer::class)
    val id: Int = 0,
    val idMensajero: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val fechaProduccion: LocalDateTime = LocalDateTime.now(),
    val jamones: List<Jamon> = listOf(),
)