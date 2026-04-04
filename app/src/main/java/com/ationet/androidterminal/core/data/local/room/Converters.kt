package com.ationet.androidterminal.core.data.local.room

import androidx.room.TypeConverter
import com.ationet.androidterminal.core.util.toLocalDateTime
import com.ationet.androidterminal.fusion.core.domain.model.PumpLockStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class Converters {
    @TypeConverter
    fun stringToLocalTimeKotlinx(value: String): LocalTime {
        return value.let { LocalTime.parse(it).toLocalDateTime().time }
    }

    @TypeConverter
    fun localTimeKotlinxToString(value: LocalTime): String {
        return value.toLocalDateTime().time.toString()
    }

    @TypeConverter
    fun stringToLocalDateKotlinx(value: String): LocalDate {
        return value.let { LocalDate.parse(it).toLocalDateTime().date }
    }

    @TypeConverter
    fun localDateKotlinxToString(value: LocalDate): String {
        return value.toLocalDateTime().date.toString()
    }

    @TypeConverter
    fun stringToLocalDateTimeKotlinx(value: String): LocalDateTime {
        return value.let {
            LocalDateTime.parse(it)
        }
    }

    @TypeConverter
    fun localDateTimeKotlinxToString(value: LocalDateTime): String {
        return value.toString()
    }
    @TypeConverter
    fun fromPumpLockStatus(status: PumpLockStatus): String {
        return status.name
    }

    @TypeConverter
    fun toPumpLockStatus(value: String): PumpLockStatus {
        return PumpLockStatus.valueOf(value)
    }
}