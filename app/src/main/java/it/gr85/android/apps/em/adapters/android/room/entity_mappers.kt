package it.gr85.android.apps.em.adapters.android.room

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.idFrom

fun Category.toAndroidRoomEntity(timestamps: Timestamps): AndroidRoomCategoryEntity =
    AndroidRoomCategoryEntity(
        id = this.id.toString(),
        name = this.name,
        hexColor = this.color.toString(),
        timestamps = timestamps
    )

fun AndroidRoomCategoryEntity.toDomain() =
    Category(
        id = idFrom( this.id ),
        name = this.name,
        color = Color.of(this.hexColor)
    )

