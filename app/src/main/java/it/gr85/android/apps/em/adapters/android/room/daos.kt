package it.gr85.android.apps.em.adapters.android.room

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Dao
import androidx.room.Update

@Dao
interface AndroidRoomCategoryDao {
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun findById(id: String): AndroidRoomCategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun findByName(name: String): AndroidRoomCategoryEntity?

    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<AndroidRoomCategoryEntity>


    @Query("SELECT COUNT(*) FROM categories WHERE id = :id")
    suspend fun exists(id: String): Int

    // TODO: OnConflic
    @Insert //(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: AndroidRoomCategoryEntity)

    @Update
    suspend fun update(category: AndroidRoomCategoryEntity)

    @Query("SELECT * FROM categories WHERE name LIKE :name")
    suspend fun search(name: String): List<AndroidRoomCategoryEntity>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: String): Boolean

    @Query("SELECT DISTINCT hex_color FROM categories")
    suspend fun getUsedColors(): Set<String>
}