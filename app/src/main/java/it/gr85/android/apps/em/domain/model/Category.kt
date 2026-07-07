package it.gr85.android.apps.em.domain.model

data class Category (
    override val id: Id,
    val name: String,
    val color: Color
) : BaseObject(id) {

    init {
        require(
            name.isNotBlank()
        )

    }

}


data class CategoryExpenseBreakdown(
    val category: Category,
    val totalAmount: MoneyAmount,
    val transactionCount: Int,
    val percentageOfTotal: Float = 0f
)
