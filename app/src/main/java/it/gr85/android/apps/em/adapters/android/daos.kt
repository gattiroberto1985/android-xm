package it.gr85.android.apps.em.adapters.android

interface CategoryDao {

    suspend fun findById(id: String): CategoryEntity?
    suspend fun getAll(): List<CategoryEntity>
    suspend fun findByName(name: String): CategoryEntity?

    suspend fun search(name: String): List<CategoryEntity>

    suspend fun insert(category: CategoryEntity)
    suspend fun update(category: CategoryEntity)
    suspend fun delete(id: String): Boolean
}

interface TransactionDao {

}
