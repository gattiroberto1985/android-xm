@file:OptIn(ExperimentalMaterial3Api::class)

package it.gr85.android.apps.em.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.gr85.android.apps.em.ui.home.components.AddTransactionDialog
import it.gr85.android.apps.em.ui.home.components.BalanceSummaryCard
import it.gr85.android.apps.em.ui.home.components.MyDp
import it.gr85.android.apps.em.ui.home.components.PieChartWithLegend
import it.gr85.android.apps.em.ui.home.components.PieSlice
import it.gr85.android.apps.em.ui.model.CategoryExpenseBreakdownUi
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

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onDateRangeSelected = { start, end -> viewModel.onDateRangeChanged(start, end) },
        onNavigateToCategoryDetail = onNavigateToCategoryDetail,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToSearch = onNavigateToSearch
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onDateRangeSelected: (start: String, end: String) -> Unit,
    onNavigateToCategoryDetail: (categoryId: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    var showAddDialog by remember { mutableStateOf(false) }

    val drawerScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    //var chartMode by remember { mutableStateOf(ChartMode.EXPENSES) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Impostazioni") },
                    selected = false,
                    onClick = {
                        onNavigateToSettings()
                        drawerScope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Ricerca") },
                    selected = false,
                    onClick = {
                        onNavigateToSearch()
                        drawerScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Home") },
                    actions = {
                        IconButton(onClick = {
                            drawerScope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Apri menu"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aggiungi transazione"
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                ) {
                    MyDp(
                        from = uiState.dateRange.start,
                        to = uiState.dateRange.end,
                        onRangeDateSelected = onDateRangeSelected
                    )

                    BalanceSummaryCard(
                        balance = uiState.dateRangeBalance,
                        totalIncome = uiState.totalIncome,
                        totalExpense = uiState.totalExpense
                    )

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                        userScrollEnabled = true
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (page) {
                                0 -> PieChartWithLegend(
                                    title = "Spese per categoria",
                                    slices = uiState.categoryExpensesBreakdown.map {
                                        PieSlice(
                                            categoryId = it.categoryId,
                                            label = it.categoryName,
                                            value = it.totalExpense.toFloat(),
                                            color = Color(it.categoryColorArgb)
                                        )
                                    },
                                    onSliceTapped = { slice ->
                                        onNavigateToCategoryDetail(slice.categoryId)
                                    }
                                )

                                1 -> PieChartWithLegend(
                                    title = "Reddito per categoria",
                                    slices = uiState.categoryExpensesBreakdown.map {
                                        PieSlice(
                                            categoryId = it.categoryId,
                                            label = it.categoryName,
                                            value = it.totalIncome.toFloat(),
                                            color = Color(it.categoryColorArgb)
                                        )
                                    },
                                    onSliceTapped = { slice ->
                                        onNavigateToCategoryDetail(slice.categoryId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { description, amount, category, transactionType ->
                // TODO: collegare a ViewModel / UseCase
                showAddDialog = false
            },
            categories = uiState.categoryExpensesBreakdown.map { it.categoryName },
            transactionTypes = listOf("Entrata", "Uscita") // TODO: this is wrong!
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview(
    onDateRangeSelected: (start: String, end: String) -> Unit = { _, _ -> },
) {
    val homeUiState = HomeUiState(
        categoryExpensesBreakdown = listOf(
            CategoryExpenseBreakdownUi(
                categoryId = "1",
                categoryName = "Food",
                categoryColorArgb = 0xFFFF0000.toInt(),
                totalAmount = 5000,
                totalIncome = 0,
                totalExpense = 5000,
                percentageOfTotal = 50f,
                transactionCount = 10
            ),
            CategoryExpenseBreakdownUi(
                categoryId = "2",
                categoryName = "Transport",
                categoryColorArgb = 0xFF00FF00.toInt(),
                totalAmount = 3000,
                totalIncome = 0,
                totalExpense = 3000,
                percentageOfTotal = 30f,
                transactionCount = 5
            ),
            CategoryExpenseBreakdownUi(
                categoryId = "3",
                categoryName = "Entertainment",
                categoryColorArgb = 0xFF0000FF.toInt(),
                totalAmount = 2000,
                totalIncome = 0,
                totalExpense = 2000,
                percentageOfTotal = 20f,
                transactionCount = 3
            )
        )
    )

    HomeScreenContent(
        uiState = homeUiState,
        snackbarHostState = SnackbarHostState(),
        onDateRangeSelected = onDateRangeSelected,
        onNavigateToCategoryDetail = {},
        onNavigateToSettings = {},
        onNavigateToSearch = {}
    )
}