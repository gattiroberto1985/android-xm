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

// un po' border line... diciamo che consideriamo il _come_ cercare gli oggetti come un aspetto
// del dominio, dato che è il _cosa_ del business. È un poo' border line, potrebbe essere un
// oggetto tecnico a livello di adapter ma può stare anche qui va'
data class TransactionSearchQuery(
    val dateRange: DateRange? = null,
    val categoryId: Id? = null,
    val type: MovementType? = null,
    val descriptionPattern: String? = null,
    val amountRange: AmountRange? = null
)


data class DateRange(val start: Date, val end: Date)
data class AmountRange(val min: Float, val max: Float)