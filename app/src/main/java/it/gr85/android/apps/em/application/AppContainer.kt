package it.gr85.android.apps.em.application

import android.content.Context
import it.gr85.android.apps.em.adapters.android.room.AppDatabase
import it.gr85.android.apps.em.adapters.android.room.ARCategoryRepository
import it.gr85.android.apps.em.adapters.android.room.ARTransactionRepository
import it.gr85.android.apps.em.application.category.GetBalanceSummary
import it.gr85.android.apps.em.application.category.GetCategoryBreakdown
import it.gr85.android.apps.em.application.transaction.AddTransactionUseCase
import it.gr85.android.apps.em.domain.ports.CategoryRepository
import it.gr85.android.apps.em.domain.ports.TransactionRepository
import it.gr85.android.apps.em.ui.home.HomeUiViewModel

/**
 * AppContainer
 *
 * Centralizza tutta la dependency injection dell'app.
 * Istanzia il database una volta e crea tutti i repository, use case, e ViewModel.
 *
 * Pattern: Service Locator / DI Container (manual, senza framework tipo Hilt)
 *
 * Usage:
 *     val container = AppContainer(context)
 *     val homeViewModel = container.createHomeUiViewModel()
 */
class AppContainer(context: Context) {

    // ==================== DATABASE ====================
    private val database: AppDatabase = AppDatabase.getDatabase(context)

    // ==================== DAO LAYER ====================
    private val categoryDao = database.categoryDao()
    private val transactionDao = database.transactionDao()

    // ==================== REPOSITORY LAYER ====================
    private val categoryRepository: CategoryRepository = ARCategoryRepository(
        arCategoryDao = categoryDao,
        now = { System.currentTimeMillis() }
    )

    private val transactionRepository: TransactionRepository = ARTransactionRepository(
        arTransactionDao = transactionDao,
        arCategoryDao = categoryDao,
        now = { System.currentTimeMillis() }
    )

    // ==================== USE CASE LAYER ====================
    // Orchestrazione della business logic

    private val getCategoryBreakdown: GetCategoryBreakdown = GetCategoryBreakdown(
        categoryRepository = categoryRepository
    )

    private val getBalanceSummary: GetBalanceSummary = GetBalanceSummary(
        transactionRepository = transactionRepository
    )

    private val addTransactionUseCase: AddTransactionUseCase = AddTransactionUseCase(
        transactionRepository = transactionRepository,
        categoryRepository = categoryRepository
    )

    // ==================== VIEW MODEL FACTORY ====================

    /**
     * Crea una nuova istanza di HomeUiViewModel.
     * Ogni volta che chiami questo, ottengo una nuova istanza (non singleton).
     * I repository sottostanti rimangono singleton.
     */
    fun createHomeUiViewModel(): HomeUiViewModel {
        return HomeUiViewModel(
            getCategoryBreakdown = getCategoryBreakdown,
            getBalanceSummary = getBalanceSummary,
            addTransactionUseCase = addTransactionUseCase
        )
    }

    // ==================== FUTURE VIEW MODELS ====================
    // Quando avrai altre screen, aggiungi qui i factory methods:
    //
    // fun createSettingsViewModel(): SettingsViewModel { ... }
    // fun createSearchViewModel(): SearchViewModel { ... }
}