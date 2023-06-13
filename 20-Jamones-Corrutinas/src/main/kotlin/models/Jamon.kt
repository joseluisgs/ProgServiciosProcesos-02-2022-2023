package models

import kotlinx.serialization.Serializable
import serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class Jamon(
    // @Serializable(with = UUIDSerializer::class)
    val id: Int = 0,
    val idGranja: String,
    val peso: Int = (6..9).random(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val fechaProduccion: LocalDateTime = LocalDateTime.now(),
)
