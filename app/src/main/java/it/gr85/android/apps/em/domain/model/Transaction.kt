package it.gr85.android.apps.em.domain.model

data class Transaction(
    override val id: Id,
    val type: MovementType,
    val amount: Float,
    val date: Date,
    val description: String,
    val category: Category
) : BaseObject(id) {

    init {
        require(
            amount != 0f &&
            description.isNotBlank()
        )

    }

}
