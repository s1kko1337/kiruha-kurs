package com.example.hometasker.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

object DateTimeUtils {

    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(dateTimeFormatter)
    }

    fun formatTime24h(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun formatTime12h(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    fun formatRelativeDate(date: LocalDate): String {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val yesterday = today.minusDays(1)

        return when (date) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            yesterday -> "Yesterday"
            else -> {
                val daysUntil = ChronoUnit.DAYS.between(today, date)
                when {
                    daysUntil in 2..6 -> date.dayOfWeek.name.lowercase()
                        .replaceFirstChar { it.uppercase() }
                    daysUntil in -6..-2 -> "Last ${date.dayOfWeek.name.lowercase()
                        .replaceFirstChar { it.uppercase() }}"
                    else -> formatDate(date)
                }
            }
        }
    }

    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, secs)
            else -> String.format("%02d:%02d", minutes, secs)
        }
    }

    fun formatDurationReadable(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    fun getStartOfWeek(date: LocalDate, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    }

    fun getEndOfWeek(date: LocalDate, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
        val lastDayOfWeek = firstDayOfWeek.minus(1)
        return date.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))
    }

    fun getStartOfMonth(date: LocalDate): LocalDate {
        return date.withDayOfMonth(1)
    }

    fun getEndOfMonth(date: LocalDate): LocalDate {
        return date.with(TemporalAdjusters.lastDayOfMonth())
    }

    fun getWeekNumber(date: LocalDate, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Int {
        val weekFields = WeekFields.of(firstDayOfWeek, 1)
        return date.get(weekFields.weekOfWeekBasedYear())
    }

    fun getDaysInMonth(date: LocalDate): List<LocalDate> {
        val start = getStartOfMonth(date)
        val end = getEndOfMonth(date)
        return generateSequence(start) { it.plusDays(1) }
            .takeWhile { !it.isAfter(end) }
            .toList()
    }

    fun getCalendarDays(
        date: LocalDate,
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
    ): List<LocalDate?> {
        val daysInMonth = getDaysInMonth(date)
        val firstDay = daysInMonth.first()
        val lastDay = daysInMonth.last()

        val leadingNulls = (firstDay.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
        val trailingNulls = (7 - (lastDay.dayOfWeek.value - firstDayOfWeek.value + 1) % 7) % 7

        return List(leadingNulls) { null } + daysInMonth + List(trailingNulls) { null }
    }

    fun isToday(date: LocalDate): Boolean = date == LocalDate.now()

    fun isTomorrow(date: LocalDate): Boolean = date == LocalDate.now().plusDays(1)

    fun isYesterday(date: LocalDate): Boolean = date == LocalDate.now().minusDays(1)

    fun isThisWeek(date: LocalDate): Boolean {
        val today = LocalDate.now()
        val startOfWeek = getStartOfWeek(today)
        val endOfWeek = getEndOfWeek(today)
        return !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
    }

    fun isThisMonth(date: LocalDate): Boolean {
        val today = LocalDate.now()
        return date.year == today.year && date.month == today.month
    }

    fun isPast(date: LocalDate): Boolean = date.isBefore(LocalDate.now())

    fun isFuture(date: LocalDate): Boolean = date.isAfter(LocalDate.now())

    fun daysBetween(start: LocalDate, end: LocalDate): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

    fun parseDate(dateString: String, pattern: String = "yyyy-MM-dd"): LocalDate? {
        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern))
        } catch (e: Exception) {
            null
        }
    }

    fun parseTime(timeString: String, pattern: String = "HH:mm"): LocalTime? {
        return try {
            LocalTime.parse(timeString, DateTimeFormatter.ofPattern(pattern))
        } catch (e: Exception) {
            null
        }
    }
}
