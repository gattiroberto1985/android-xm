package it.gr85.android.apps.em.ui

import it.gr85.android.apps.em.domain.model.MoneyAmount
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Date
import java.util.Locale

object MoneyFormatter {
    fun format(amount: Long?,
               currency: Currency = Currency.getInstance(Locale.getDefault())): String =
        amount?.let { "${it / 100.0} ${currency.symbol}" } ?: "—"
}

object TextViewDateFormatter {

    fun format(date: LocalDate?, locale: Locale = Locale.getDefault()): String {
        if (date == null) return "—"
        val formatter = DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.SHORT)
            .withLocale(locale)
        return date.format(formatter)
    }
}