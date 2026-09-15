package it.gr85.android.apps.em.adapters.android.room

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Dao
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ARCategoryDao {
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun findById(id: String): ARCategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun findByName(name: String): ARCategoryEntity?

    @Query("SELECT * FROM categories WHERE id in (:ids)")
    suspend fun getByIds(ids: List<String>): List<ARCategoryEntity>

    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<ARCategoryEntity>

    @Insert
    suspend fun insert(category: ARCategoryEntity)

    @Update
    suspend fun update(category: ARCategoryEntity)

    @Query("SELECT * FROM categories WHERE name LIKE :name")
    suspend fun search(name: String): List<ARCategoryEntity>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT DISTINCT hex_color FROM categories")
    suspend fun getUsedColors(): List<String>

    @Query("SELECT * FROM categories ORDER BY name ASC") // occhio che va bene per robe "piccole" ( centinaia di righe)
    fun observeAll(): Flow<List<ARCategoryEntity>> // non si può avere suspend e flow insieme, perché flow è "lazy" e non si può sospendere la creazione del flow stesso, ma solo la sua emissione

    @Query("""
        SELECT 
            c.id,
            c.name,
            c.hex_color,
            t.type,
        COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN CAST(t.amount AS REAL) ELSE 0 END), 0) as total_income,
        COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN CAST(t.amount AS REAL) ELSE 0 END), 0) as total_expense,
        COALESCE(COUNT(t.id), 0) as transaction_count
        FROM categories c
        LEFT JOIN transactions t ON c.id = t.category_id
        WHERE t.date BETWEEN :startDateEpoch AND :endDateEpoch
        GROUP BY c.id, c.name, c.hex_color, t.type
        ORDER BY c.name ASC, t.type ASC
    """)
    suspend fun getExpenseBreakdown(
        startDateEpoch: Long,
        endDateEpoch: Long
    ): List<CategoryExpenseRow>

}