@file:OptIn(ExperimentalMaterial3Api::class)

package it.gr85.android.apps.em.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import it.gr85.android.apps.em.domain.model.DateRange
import it.gr85.android.apps.em.ui.AppColors
import it.gr85.android.apps.em.ui.AppShapes
import it.gr85.android.apps.em.ui.AppSpacing
import it.gr85.android.apps.em.ui.AppTypography
import it.gr85.android.apps.em.ui.home.components.AddTransactionDialog
import it.gr85.android.apps.em.ui.home.components.BalanceSummaryCard
import it.gr85.android.apps.em.ui.home.components.MyDp
import it.gr85.android.apps.em.ui.home.components.PieChartWithLegend
import it.gr85.android.apps.em.ui.home.components.PieSlice
import it.gr85.android.apps.em.ui.model.CategoryExpenseBreakdownUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// region SCREEN ROOT

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

// endregion SCREEN ROOT

// region SCREEN CONTENT

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onDateRangeSelected: (start: String, end: String) -> Unit,
    onNavigateToCategoryDetail: (categoryId: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val drawerScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var chartMode by remember { mutableStateOf(ChartMode.EXPENSES) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeScreenDrawerContent(
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToSearch = onNavigateToSearch,
                drawerScope = drawerScope,
                drawerState = drawerState
            )
        }
    ) {
        Scaffold(
            topBar = {
                HomeScreenTopBar(
                    drawerScope = drawerScope,
                    drawerState = drawerState
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
                    .background(AppColors.Background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .zIndex(1f)
                ) {
                    HomeScreenTopContent(
                        dateRange = uiState.dateRange,
                        onDateRangeSelected = onDateRangeSelected,
                        balance = uiState.dateRangeBalance,
                        totalIncome = uiState.totalIncome,
                        totalExpense = uiState.totalExpense
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.lg))

                    HomeScreenChartSection(
                        chartMode = chartMode,
                        onChartModeChanged = { chartMode = it },
                        categoryBreakdown = uiState.categoryExpensesBreakdown,
                        onNavigateToCategoryDetail = onNavigateToCategoryDetail
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.xl))
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
            movementTypes = uiState.availableMovementTypes
        )
    }
}

// endregion SCREEN CONTENT

// region SUB COMPONENTS

@Composable
private fun HomeScreenTopBar(
    drawerScope: CoroutineScope,
    drawerState: DrawerState
) {
    TopAppBar(
        title = {
            Text(
                "Home",
                style = AppTypography.TitleLarge
            )
        },
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
}

@Composable
private fun HomeScreenDrawerContent(
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    drawerScope: CoroutineScope,
    drawerState: DrawerState
) {
    ModalDrawerSheet {
        Text(
            text = "Menu",
            style = AppTypography.HeadlineMedium,
            modifier = Modifier.padding(AppSpacing.lg)
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.md),
            color = AppColors.Divider,
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        NavigationDrawerItem(
            label = {
                Text(
                    "Impostazioni",
                    style = AppTypography.BodyLarge
                )
            },
            selected = false,
            onClick = {
                onNavigateToSettings()
                drawerScope.launch { drawerState.close() }
            }
        )

        NavigationDrawerItem(
            label = {
                Text(
                    "Ricerca",
                    style = AppTypography.BodyLarge
                )
            },
            selected = false,
            onClick = {
                onNavigateToSearch()
                drawerScope.launch { drawerState.close() }
            }
        )
    }
}

@Composable
private fun HomeScreenTopContent(
    dateRange: DateRange,
    onDateRangeSelected: (start: String, end: String) -> Unit,
    balance: Long,
    totalIncome: Long,
    totalExpense: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md)
    ) {
        MyDp(
            from = dateRange.start,
            to = dateRange.end,
            onRangeDateSelected = onDateRangeSelected
        )

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppColors.SurfaceElevated,
                    shape = AppShapes.Medium
                )
                .padding(AppSpacing.md)
        ) {
            BalanceSummaryCard(
                balance = balance,
                totalIncome = totalIncome,
                totalExpense = totalExpense
            )
        }
    }
}

@Composable
private fun HomeScreenChartSection(
    chartMode: ChartMode,
    onChartModeChanged: (ChartMode) -> Unit,
    categoryBreakdown: List<CategoryExpenseBreakdownUi>,
    onNavigateToCategoryDetail: (categoryId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md)
    ) {
        // Chart mode toggle buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppSpacing.md,
                    vertical = AppSpacing.md
                )
                .background(
                    color = AppColors.SurfaceElevated,
                    shape = AppShapes.Medium
                )
                .padding(AppSpacing.md),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = chartMode == ChartMode.EXPENSES,
                onClick = { onChartModeChanged(ChartMode.EXPENSES) },
                label = {
                    Text(
                        "Spese",
                        style = AppTypography.LabelMedium
                    )
                }
            )
            Spacer(modifier = Modifier.width(AppSpacing.md))
            FilterChip(
                selected = chartMode == ChartMode.INCOME,
                onClick = { onChartModeChanged(ChartMode.INCOME) },
                label = {
                    Text(
                        "Reddito",
                        style = AppTypography.LabelMedium
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Chart rendering
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = AppColors.Surface,
                    shape = AppShapes.Large
                )
                .padding(AppSpacing.lg),
            contentAlignment = Alignment.Center
        ) {
            val (title, valueExtractor) = when (chartMode) {
                ChartMode.EXPENSES -> "Spese per categoria" to { bd: CategoryExpenseBreakdownUi -> bd.totalExpense }
                ChartMode.INCOME -> "Reddito per categoria" to { bd: CategoryExpenseBreakdownUi -> bd.totalIncome }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = AppTypography.TitleLarge,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(bottom = AppSpacing.md)
                )

                PieChartWithLegend(
                    title = "",  // Titolo gestito qui sopra
                    slices = categoryBreakdown.map { breakdown ->
                        PieSlice(
                            categoryId = breakdown.categoryId,
                            label = breakdown.categoryName,
                            value = valueExtractor(breakdown).toFloat(),
                            color = Color(breakdown.categoryColorArgb)
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

// endregion SUB COMPONENTS

// region PREVIEWS

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
                categoryColorArgb = 0xFFFF6B6B.toInt(),
                totalAmount = 5000,
                totalIncome = 0,
                totalExpense = 5000,
                percentageOfTotal = 50f,
                transactionCount = 10
            ),
            CategoryExpenseBreakdownUi(
                categoryId = "2",
                categoryName = "Transport",
                categoryColorArgb = 0xFF4ECDC4.toInt(),
                totalAmount = 3000,
                totalIncome = 0,
                totalExpense = 3000,
                percentageOfTotal = 30f,
                transactionCount = 5
            ),
            CategoryExpenseBreakdownUi(
                categoryId = "3",
                categoryName = "Entertainment",
                categoryColorArgb = 0xFF95E1D3.toInt(),
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

// endregion PREVIEWS