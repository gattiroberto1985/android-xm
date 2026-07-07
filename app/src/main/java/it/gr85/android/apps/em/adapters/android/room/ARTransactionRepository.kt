package it.gr85.android.apps.em.adapters.android.room

import it.gr85.android.apps.em.application.category.CategoryNotFoundException
import it.gr85.android.apps.em.application.exceptions.InvalidDateRangeException
import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.Id
import it.gr85.android.apps.em.domain.model.Transaction
import it.gr85.android.apps.em.domain.model.TransactionSearchQuery
import it.gr85.android.apps.em.domain.ports.TransactionRepository

class ARTransactionRepository(
    private val arTransactionDao : ARTransactionDao,
    private val arCategoryDao    : ARCategoryDao,
    private val now: () -> Long = { System.currentTimeMillis() }
) : TransactionRepository {

    override suspend fun getById(id: Id): Transaction? {
        val txEntity = arTransactionDao.getById(id.toString()) ?: return null
        val categoryEntity = arCategoryDao.findById(txEntity.categoryId) ?: throw CategoryNotFoundException()
        return txEntity.toDomain(categoryEntity.toDomain())
    }

    override suspend fun getByCategory(category: Category): List<Transaction> {
        val categoryEntity = arCategoryDao.findById(category.id.toString()) ?: throw CategoryNotFoundException()
        return arTransactionDao.getByCategory(categoryEntity.id).map{ it -> it.toDomain(categoryEntity.toDomain())}
    }

    override suspend fun getByDateRange(
        startDate: Date?,
        endDate: Date?
    ): List<Transaction> {
        if( startDate == null && endDate == null ) {
            throw InvalidDateRangeException()
        }

        /*
         val transactionEntities : List<ARTransactionEntity>
         if ( endDate != null && startDate != null ) {
            transactionEntities = arTransactionDao.getByDateRange(startDate.toEpochDay(), endDate.toEpochDay())
        } else if ( startDate != null ) {
                transactionEntities = arTransactionDao.getByDateAfter(startDate.toEpochDay())
        } else {
            transactionEntities = arTransactionDao.getByDateBefore(endDate.toEpochDay())
        }*/
        val transactionEntities = when {
            startDate != null && endDate != null ->
                arTransactionDao.getByDateRange(startDate.toEpochDay(), endDate.toEpochDay())
            startDate != null ->
                arTransactionDao.getByDateAfter(startDate.toEpochDay())
            else ->
                arTransactionDao.getByDateBefore(endDate!!.toEpochDay()) // KotlinNullPointerException will be launched if null!
        }
        // evito una query, se non ho transazioni
        if (transactionEntities.isEmpty()) {
            return emptyList()
        }
        return enrichWithCategories(transactionEntities)
    }


    override suspend fun search(tsq: TransactionSearchQuery): List<Transaction> {
        val txEntities = arTransactionDao.searchTransactions(
            dateStart = tsq.dateRange?.start?.toEpochDay(),
            dateEnd = tsq.dateRange?.end?.toEpochDay(),
            categoryId = tsq.categoryId.toString(),
            type = tsq.type?.toString(),
            descriptionQuery = tsq.descriptionPattern,
            minAmount = tsq.amountRange?.min,
            maxAmount = tsq.amountRange?.max
        )

        if (txEntities.isEmpty()) return emptyList()
        return enrichWithCategories(txEntities)
    }


    private suspend fun enrichWithCategories(
        transactionEntities: List<ARTransactionEntity>
    ): List<Transaction> {
        val categoriesById = arCategoryDao.getByIds(
            transactionEntities.map { it.categoryId }.distinct()
        ).associateBy { it.id }

        return transactionEntities.map { tx ->
            val category = categoriesById[tx.categoryId]
                ?: throw CategoryNotFoundException()
            tx.toDomain(category.toDomain())
        }
    }


    @androidx.room.Transaction
    override suspend fun save(t: Transaction): Transaction {

        val te = t.toAndroidRoomEntity(Timestamps( now(), now()))

        val existingTransactionEntity = arTransactionDao.getById(t.id.toString())

        if( existingTransactionEntity != null ) {
            val updatedEntity = te.copy(
                timestamps = Timestamps(
                    created_at = existingTransactionEntity.timestamps.created_at,
                    updated_at = now()
                )
            )
            arTransactionDao.update(updatedEntity)
        } else {
            arTransactionDao.insert(te)
        }
        return t
    }


    @androidx.room.Transaction
    override suspend fun updateCategoryForAll(
        transactionIds: List<Id>,
        newCategory: Category
    ) : Int {
        return arTransactionDao.updateCategoryFor( transactionIds.map{ it.toString() }, newCategory.id.toString() )
    }


    @androidx.room.Transaction // we have a name collision, damn!!
    override suspend fun reassignTransactionsByCategory(
        fromCategory: Category,
        toCategory: Category
    ) {
        arTransactionDao.reassignTransactionsByCategory( fromCategory.id.toString(), toCategory.id.toString() )
    }

}