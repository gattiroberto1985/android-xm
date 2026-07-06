package it.gr85.android.apps.em.adapters.android.room

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.MovementType
import it.gr85.android.apps.em.domain.model.Transaction
import it.gr85.android.apps.em.domain.model.idFrom
import java.time.LocalDate

fun Category.toAndroidRoomEntity(timestamps: Timestamps): ARCategoryEntity =
    ARCategoryEntity(
        id = this.id.toString(),
        name = this.name,
        hexColor = this.color.toString(),
        timestamps = timestamps
    )

fun ARCategoryEntity.toDomain() =
    Category(
        id = idFrom( this.id ),
        name = this.name,
        color = Color.of(this.hexColor)
    )


fun Transaction.toAndroidRoomEntity(timestamps: Timestamps): ARTransactionEntity =
    ARTransactionEntity(
        id = this.id.toString(),
        type = this.type.toString(),
        timestamps = timestamps,
        amount = this.amount,
        date = this.date.toEpochDay(),
        description = description,
        category_id = this.category.id.toString()
    )

fun ARTransactionEntity.toDomain(c: Category) =
    Transaction(
        id = idFrom( this.id ),
        type = MovementType.valueOf( this.type),
        amount = this.amount,
        date = Date.ofEpochDay(this.date),
        description = this.description,
        category = c

    )

