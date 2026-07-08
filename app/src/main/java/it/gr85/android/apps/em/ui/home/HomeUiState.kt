package it.gr85.android.apps.em.ui.home

import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.domain.model.DateRange
import it.gr85.android.apps.em.ui.model.CategoryExpenseBreakdownUi

data class HomeUiState (
    val legendExpanded            : Boolean = true,
    val isLoading                 : Boolean = false,
    val categoryExpensesBreakdown : List<CategoryExpenseBreakdownUi> = emptyList(),
    val dateRange                 : DateRange = DateRange.default30Days(),
    val balance                   : Long? = null,
    val error                     : String? = null
) {
    val balanceFormatted: String get() =
        if (balance != null) {
            "€${balance / 100.0}"
        } else {
            "—"
        }

    val totalExpense: Long get() =
        categoryExpensesBreakdown.sumOf { it.totalAmount }
}