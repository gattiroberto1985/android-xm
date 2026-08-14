package it.gr85.android.apps.em.ui.home

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.ui.home.components.MyDp
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeUiViewModel,
    onNavigateToCategoryDetail: (categoryId: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf( false )  }

    // EFFETTO 1: Navigazione (richiede viewModel)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.OnCategoryTap -> {
                    onNavigateToCategoryDetail(event.categoryId)
                }
                is HomeUiEvent.OnSettingsClick -> {
                    onNavigateToSettings()
                }
                is HomeUiEvent.OnSearchClick -> {
                    onNavigateToSearch()
                }
            }
        }
    }

    // EFFETTO 2: Snackbar (richiede snackbarHostState)
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        isDatePickerOpen = showDatePicker,
        snackbarHostState = snackbarHostState,
        onDateRangeSelected = { start, end -> viewModel.onDateRangeChanged(start, end) },
        onCloseDatePicker = { showDatePicker = false },
        onShowDatePicker = { showDatePicker = true },
        onNavigateToCategoryDetail = onNavigateToCategoryDetail,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToSearch = onNavigateToSearch
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    isDatePickerOpen: Boolean,
    onDateRangeSelected: (start: String, end: String) -> Unit,
    onCloseDatePicker: () -> Unit,
    onShowDatePicker: () -> Unit,
    onNavigateToCategoryDetail: (categoryId: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    snackbarHostState: SnackbarHostState
) {

    if ( isDatePickerOpen ) {
        MyDp(
            from = uiState.dateRange.start,
            to = uiState.dateRange.end,
            onRangeDateSelected = onDateRangeSelected,
            onCloseDatePicker = onCloseDatePicker
        )
    }

    /*Button(
        onClick = {
            snackScope.launch {
                snackbarHostState.showSnackbar("Hello from snackbar!")
            }
            Log.i( "EMBOB", "Button clicked, launching snackbar notification!" )
        }
    ) {
        Log.i( "EMBOB", "Button clicked, launching snackbar notification!" )
        Text(text = "Send a notification in the snackbar")
    }*/
}

@Preview
@Composable
fun HomeScreenPreview(
    onDateRangeSelected: (start: String, end: String) -> Unit = { _, _ -> },
    /*onCloseDatePicker: () -> Unit = {},
    onShowDatePicker: () -> Unit = {},*/
) {
    var showDatePicker by remember { mutableStateOf( true )  }

    HomeScreenContent(
        uiState = HomeUiState(),
        isDatePickerOpen = showDatePicker,
        snackbarHostState = SnackbarHostState(),
        onDateRangeSelected = onDateRangeSelected,
        onCloseDatePicker = { showDatePicker = false },
        onShowDatePicker = { showDatePicker = true },
        onNavigateToCategoryDetail = {},
        onNavigateToSettings = {},
        onNavigateToSearch = {}
    )
}