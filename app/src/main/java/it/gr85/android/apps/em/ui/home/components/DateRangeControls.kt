package it.gr85.android.apps.em.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import it.gr85.android.apps.em.ui.AppSpacing

/*
|-----------------------------------|
|                                   |
|                FROM: __ / __ / __ |
|                  TO: __ / __ / __ |
|                                   |
|-----------------------------------|
 */
@Composable
fun DateRangeControls(
    startDate: String ,
    endDate: String,
    onDateRangeChanged: (startDate: String, endDate: String) -> Unit,
    modifier : Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        DatePickerComponent(
            label = "FROM",
            date = startDate,
            onDateChanged = { newStartDate : String ->
                onDateRangeChanged(newStartDate, endDate)
            }
        )

        DatePickerComponent(
            label = "TO",
            date = endDate,
            onDateChanged = { newEndDate : String ->
                onDateRangeChanged(startDate, newEndDate)
            }
        )

    }
}

@Composable
fun DatePickerComponent(
    label: String,
    date: String,
    onDateChanged: (date: String) -> Unit,
    modifier : Modifier = Modifier
) {
    Row() {

    }
}

@Preview
@Composable
fun DateRangeControlsPreview() {
    DateRangeControls(
        startDate = "2023-01-01",
        endDate = "2023-12-31",
        onDateRangeChanged = { _, _ -> }
    )
}