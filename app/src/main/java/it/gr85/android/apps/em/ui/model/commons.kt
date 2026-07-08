package it.gr85.android.apps.em.ui.model

data class CategoryExpenseBreakdownUi(
    val categoryId: String,
    val categoryName: String,
    val categoryColorArgb: Int,  // android.graphics.Color int
    val totalAmount: Long,       // in cents
    val percentageOfTotal: Float,  // 0-100
    val transactionCount: Int
) {
    val totalAmountFormatted: String get() = "€${totalAmount / 100.0}"
}