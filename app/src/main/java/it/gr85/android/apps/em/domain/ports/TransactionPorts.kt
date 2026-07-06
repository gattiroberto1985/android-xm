package it.gr85.android.apps.em.domain.ports

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.Id
import it.gr85.android.apps.em.domain.model.Transaction
import it.gr85.android.apps.em.domain.model.TransactionSearchQuery

interface TransactionRepository {

    suspend fun getById      ( id: Id): Transaction?
    suspend fun getByCategory(category: Category): List<Transaction>
    suspend fun getByDateRange(startDate: Date?, endDate : Date? ): List<Transaction>
    suspend fun search       (tsq : TransactionSearchQuery): List<Transaction>

    suspend fun save( t: Transaction ): Transaction

    suspend fun updateCategoryForAll(transactionIds: List<Id>, newCategory: Category): Int

    suspend fun reassignTransactionsByCategory(fromCategory: Category, toCategory: Category )
}
