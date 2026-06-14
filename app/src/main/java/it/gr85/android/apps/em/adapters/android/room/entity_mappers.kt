package it.gr85.android.apps.em.adapters.android.room

import it.gr85.android.apps.em.adapters.android.AndroidRoomCategoryEntity
import it.gr85.android.apps.em.domain.model.Category

fun Category.toEntity(): AndroidRoomCategoryEntity =
    AndroidRoomCategoryEntity(
        id = this.id.value,
        name = this.nome,
        hexColor = this.colore
    )