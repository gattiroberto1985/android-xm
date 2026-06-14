package it.gr85.android.apps.em.adapters.android.room

import it.gr85.android.apps.em.adapters.android.AndroidRoomCategoryEntity
import it.gr85.android.apps.em.adapters.android.CategoryEntity
import it.gr85.android.apps.em.adapters.android.toAndroidRoomEntity
import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Id
import it.gr85.android.apps.em.domain.model.isValidId
import it.gr85.android.apps.em.domain.ports.CategoryRepository

class AndroidRoomCategoryRepositoryImpl(
    private val categoryDao: AndroidRoomCategoryEntity  // ← agnostico! accetta qualsiasi impl
) : CategoryRepository {
    override suspend fun getById(id: Id): Category? {
        return categoryDao.findById(id.toString())?.toDomain()
    }

    override suspend fun getAll(): List<Category> {
        return categoryDao.getAll().map { ce : CategoryEntity -> ce.toDomain() }
    }

    override suspend fun search(name: String): List<Category> {
        return categoryDao.search(name).map { ce : CategoryEntity -> ce.toDomain() }
    }

    override suspend fun save(category: Category): Category {
        if( isValidId(category.id) ) {
            categoryDao.update( category )
            return category
        }
        return categoryDao.insert( category.toAndroidRoomEntity() )
    }

    override suspend fun delete(id: Id) {
        return categoryDao.delete( id.toString() )
    }
}