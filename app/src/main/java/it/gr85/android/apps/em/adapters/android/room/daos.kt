package it.gr85.android.apps.em.adapters.android.room

import androidx.room.vo.Dao
import it.gr85.android.apps.em.adapters.android.CategoryDao
import it.gr85.android.apps.em.adapters.android.CategoryEntity


@Dao
interface AndroidRoomCategoryDao : CategoryDao {
    @Query("SELECT * FROM categories WHERE id = :id")
    override suspend fun findById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name")
    override suspend fun findByName(name: String): CategoryEntity?

    @Query("SELECT COUNT(*) FROM categories WHERE id = :id")
    suspend fun exists(id: String): Int

    // TODO: OnConflic
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE nome LIKE :name")
    override suspend fun search(name: String): List<CategoryEntity>

    @Query("DELETE FROM categories WHERE id = :id")
    override suspend fun delete(id: String): Boolean
}