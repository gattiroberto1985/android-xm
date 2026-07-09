package it.gr85.android.apps.em.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    /*viewModel: HomeUiViewModel,
    onNavigateToCategoryDetail: (categoryId: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,*/
) {

    //// region FORGET FOR NOW
    //
    //// observe sullo stato del viewModel!
    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    //
    //// observe per gli eventi
    //LaunchedEffect(Unit) {
    //    viewModel.uiEvent.collect { event ->
    //        when (event) {
    //            is HomeUiEvent.OnCategoryTap -> {
    //                onNavigateToCategoryDetail(event.categoryId)
    //            }
    //            is HomeUiEvent.OnSettingsClick -> {
    //                onNavigateToSettings()
    //            }
    //            is HomeUiEvent.OnSearchClick -> {
    //                onNavigateToSearch()
    //            }
    //        }
    //    }
    //}
    //
    //// endregion FORGET FOR NOW

    Text(
        text = "Hello from compose!"
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}