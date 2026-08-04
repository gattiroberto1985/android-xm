package it.gr85.android.apps.em.ui.home.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MyDp(
    from: LocalDate = LocalDate.now().minusDays(30),
    to: LocalDate = LocalDate.now(),
    onRangeDateSelected: (startDate: String, endDate: String) -> Unit = { _, _ -> }
) {

    val state =
        rememberDateRangePickerState( // Datepicker è un componente nativo di android con il
                                      // relativo stage
            initialSelectedStartDate = from,
            initialSelectedEndDate = to,
        )

    /*
    // Lo snackbar è un componente lightweight in fondo alla ui dove vivono le notifiche temporanee

    val snackState = remember { SnackbarHostState() } // Lo state dello snackbar. Il remember salva in
                                                      // maniera persistente e safe durante le
                                                      // ricomposizioni della ui lo state.

    val snackScope = rememberCoroutineScope() // Lanciare le notifiche significa lanciare delle
                                              // coroutine, ma per farlo serve uno scope! Eccolo qua!

    SnackbarHost(hostState = snackState, Modifier.zIndex(1f)) // Il componente composable che mostra lo snackbar
    */
    // Creates a state with pre-selected date range.


    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        // Add a row with "Save" and dismiss actions.
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .background(DatePickerDefaults.colors().containerColor)
                    .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = {
                    PlainTooltip(
                        modifier =
                            Modifier.semantics {
                                // TODO(b/496338253): Remove this modifier once bug where tooltip
                                //  text is not announced by a11y screen readers is resolved.
                                liveRegion = LiveRegionMode.Assertive
                                paneTitle = "Close"
                            }
                    ) {
                        Text("Close")
                    }
                },
                state = rememberTooltipState(),
            ) {
                IconButton(onClick = { /* dismiss the UI */ }) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }
            TextButton(
                onClick = {
                    onRangeDateSelected(
                        state.getSelectedStartDate().toString(),
                        state.getSelectedEndDate().toString()
                    )
                },
                /*onClick = {
                    snackScope.launch {
                        val range = state.getSelectedStartDate()!!..state.getSelectedEndDate()!!
                        snackState.showSnackbar("Saved range: $range")
                    }
                },*/
                enabled = state.getSelectedEndDate() != null,
            ) {
                Text(text = "Save")
            }
        }
        DateRangePicker(state = state, modifier = Modifier.weight(1f))
    }
}