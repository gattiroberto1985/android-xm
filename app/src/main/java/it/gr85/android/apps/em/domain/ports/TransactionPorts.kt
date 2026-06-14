package it.gr85.android.apps.em.domain.ports

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.Id
import it.gr85.android.apps.em.domain.model.Transaction

interface MovementRepository {

    fun getById      ( id: Id): Transaction?
    fun getByCategory(category: Category): List<Transaction>
    fun getByDate    (startDate: Date, endDate : Date? ): List<Transaction>
    fun search       (category: Category?, startDate: Date?, endDate: Date?, description: String?): List<Transaction>

}
