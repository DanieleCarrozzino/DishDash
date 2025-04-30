package com.carrozzino.dishdash.ui.utility

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getRemainingDaysWithDates(): List<String> {
    var today = LocalDate.now()

    if(DayOfWeek.SUNDAY.ordinal - today.dayOfWeek.ordinal < 2) {
        today = today.plusDays(((DayOfWeek.SUNDAY.ordinal - today.dayOfWeek.ordinal) + 1).toLong())
    }

    val formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM")

    return (0..DayOfWeek.FRIDAY.ordinal - today.dayOfWeek.ordinal)
        .map { today.plusDays(it.toLong()) }
        .map { it.format(formatter) }
}

fun getActualDate(): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM")
    return today.format(formatter)
}