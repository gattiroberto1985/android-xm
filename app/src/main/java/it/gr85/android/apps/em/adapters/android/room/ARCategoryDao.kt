package it.gr85.android.apps.em.adapters.android.room

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Dao
import androidx.room.Update

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
    suspend fun delete(id: String): Boolean

    @Query("SELECT DISTINCT hex_color FROM categories")
    suspend fun getUsedColors(): Set<String>
}