package com.example.hometasker.data.local.database.converter

import androidx.room.TypeConverter
import com.example.hometasker.domain.model.Priority
import com.example.hometasker.domain.model.RepeatType
import com.example.hometasker.domain.model.TrackingStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Конвертеры типов для Room Database
 */
class Converters {

    // LocalDateTime
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    // LocalDate
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    // LocalTime
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    // Priority
    @TypeConverter
    fun fromPriority(value: Priority): String {
        return value.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }

    // RepeatType
    @TypeConverter
    fun fromRepeatType(value: RepeatType): String {
        return value.name
    }

    @TypeConverter
    fun toRepeatType(value: String): RepeatType {
        return RepeatType.valueOf(value)
    }

    // TrackingStatus
    @TypeConverter
    fun fromTrackingStatus(value: TrackingStatus): String {
        return value.name
    }

    @TypeConverter
    fun toTrackingStatus(value: String): TrackingStatus {
        return TrackingStatus.valueOf(value)
    }
}
