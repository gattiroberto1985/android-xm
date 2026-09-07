package it.gr85.android.apps.em.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import it.gr85.android.apps.em.ui.TextViewDateFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDp(
    from: LocalDate = LocalDate.now().minusDays(30),
    to: LocalDate = LocalDate.now(),
    onRangeDateSelected: (startDate: String, endDate: String) -> Unit = { _, _ -> },
) {

    var showDatePicker by remember { mutableStateOf(false) }

    val state = rememberDateRangePickerState(
        initialSelectedStartDate = from,
        initialSelectedEndDate = to,
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = { showDatePicker = true })
    ) {
        // usare interfaccia TextFormatter con metodo format
        Text(text = TextViewDateFormatter.format(state.getSelectedStartDate() ) )
        Text(text = " - ")
        Text(text = TextViewDateFormatter.format(state.getSelectedEndDate() ) )
    }

    if ( !showDatePicker ) {
        return
    }
    // else : mostra il date range picker!
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DatePickerDefaults.colors().containerColor)
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = { showDatePicker = false }) {  // ← Ora funziona!
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
            TextButton(
                onClick = {
                    onRangeDateSelected(
                        TextViewDateFormatter.format(state.getSelectedStartDate() ),
                        TextViewDateFormatter.format(state.getSelectedEndDate() )
                    )
                    showDatePicker = false  // ← Auto-chiudi dopo salvataggio
                },
                enabled = state.getSelectedEndDate() != null,
            ) {
                Text(text = "Save")
            }
        }
        DateRangePicker(state = state, modifier = Modifier.weight(1f))
    }
}