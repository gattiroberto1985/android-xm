package it.gr85.android.apps.em.adapters.android.room

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface ARTransactionDao {

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById( id: String): ARTransactionEntity?

    @Query("SELECT * FROM transactions WHERE description LIKE '%:description:%'")
    suspend fun getByDescription( description: String): List<ARTransactionEntity>

    @Query("SELECT * FROM transactions WHERE date >= :startDate")
    suspend fun getByDateAfter( startDate: Long): List<ARTransactionEntity>

    @Query( "SELECT * FROM transactions WHERE date <= :endDate")
    suspend fun getByDateBefore( endDate: Long): List<ARTransactionEntity>

    @Query( "SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getByDateRange( startDate: Long, endDate: Long): List<ARTransactionEntity>

    @Query( "SELECT * FROM transactions WHERE category_id = :categoryId")
    suspend fun getByCategory( categoryId : String): List<ARTransactionEntity>

    @Query("""
        SELECT * FROM transactions
        WHERE 
            (:dateStart IS NULL OR date >= :dateStart)
            AND (:dateEnd IS NULL OR date < :dateEnd)
            AND (:categoryId IS NULL OR category_id = :categoryId)
            AND (:type IS NULL OR type = :type)
            AND (:descriptionQuery IS NULL OR description LIKE '%' || :descriptionQuery || '%')
            AND (:minAmount IS NULL OR amount >= :minAmount)
            AND (:maxAmount IS NULL OR amount <= :maxAmount)
        ORDER BY date DESC
    """)
    suspend fun searchTransactions(
        dateStart: Long?,
        dateEnd: Long?,
        categoryId: String?,
        type: String?,
        descriptionQuery: String?,
        minAmount: Float?,
        maxAmount: Float?
    ): List<ARTransactionEntity>

    @Insert
    suspend fun insert( te: ARTransactionEntity): Long

    @Update
    suspend fun update( te: ARTransactionEntity): Long

    @Query("""
    UPDATE transactions 
    SET category_id = :toCategoryId 
    WHERE category_id = :fromCategoryId
    """)
    suspend fun reassignTransactionsByCategory( fromCategoryId: String, toCategoryId: String ): Int

    @Query("""
        UPDATE transactions
        SET category_id = :newCategoryId
        WHERE id in ( :tIds)
    """)
    suspend fun updateCategoryFor(tIds: List<String>, newCategoryId: String) : Int
}