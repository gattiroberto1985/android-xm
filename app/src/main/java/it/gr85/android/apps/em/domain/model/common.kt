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


enum class MovementType {
    INCOME,
    EXPENSE
}


open class BaseObject(
    open val id: Id
)


@JvmInline
value class Color private constructor(
    val value: String
) {

    companion object {

        private val REGEX =
            // Regex("^#(?:[0-9a-fA-F]{6})$")
            Regex("^#[0-9a-fA-F]{6}$")

        fun of(raw: String): Color {

            require(REGEX.matches(raw)) {
                "Invalid color format"
            }

            return Color(raw.uppercase())
        }
    }
}

public class Constants {
    companion object {
        const val DEFAULT_CATEGORY_NAME = "OTHER" // TODO: how to improve??
    }
}
