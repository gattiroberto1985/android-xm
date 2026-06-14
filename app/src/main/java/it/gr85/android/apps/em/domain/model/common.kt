package it.gr85.android.apps.em.domain.model

import java.time.LocalDate
import java.util.UUID


// region ID DEFINITION
typealias Id = UUID
typealias Date = LocalDate

fun newId(): Id = UUID.randomUUID()
fun idFrom(s: String): Id = UUID.fromString(s)
fun isValidId( id: Id) : Boolean = run {
    return id == null ||
            id.toString().trim() == ""
}

// endregion ID DEFINITION


enum class MovementType {
    INCOME,
    EXPENSE
}


open class BaseObject(
    open val id: Id
)
