package it.gr85.android.apps.em.adapters.android.room

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.Id
import it.gr85.android.apps.em.domain.ports.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

// Questa interfaccia serve per sfruttare i Flow di android.
interface ReactiveCategoryRepository {
    suspend fun observeAll(): Flow<List<Category>>
}

class ARCategoryRepository(
    private val arCategoryDao: ARCategoryDao,
    private val now: () -> Long = { System.currentTimeMillis() }
) : CategoryRepository, ReactiveCategoryRepository {

    override suspend fun getById(id: Id): Category? {
        return arCategoryDao.findById(id.toString())?.toDomain()
    }

    override suspend fun getByName( name: String): Category? {
        return arCategoryDao.findByName( name )?.toDomain()
    }


    override suspend fun getAll(): List<Category> {
        return arCategoryDao.getAll().map ( ARCategoryEntity::toDomain )
    }

    override suspend fun getUsedColors(): Set<Color> {
        return arCategoryDao.getUsedColors().map(Color::of).toSet()
    }

    override suspend fun search(name: String): List<Category> {
        return arCategoryDao.search(name).map ( ARCategoryEntity::toDomain )
    }

    override suspend fun save(category: Category): Category {

        val ce = category.toAndroidRoomEntity(Timestamps( now(), now()))

        val existingCategoryEntity = arCategoryDao.findById(category.id.toString())

        if( existingCategoryEntity != null ) {
            val updatedEntity = ce.copy(
                timestamps = Timestamps(
                    created_at = existingCategoryEntity.timestamps.created_at,
                    updated_at = now() // System.currentTimeMillis()
                )
            )
            arCategoryDao.update(updatedEntity)
        } else {
            arCategoryDao.insert(ce)
        }
        return category
    }

    override suspend fun delete(id: Id) {
        arCategoryDao.delete( id.toString() )
    }

    // va bene per robe con poche righe, sennò bisogna fare altri giri
    override suspend fun observeAll(): Flow<List<Category>> {
        return arCategoryDao.observeAll().map { roomCategories ->
            roomCategories.map { it.toDomain() }
        }
    }

    override suspend fun getExpenseBreakdown( startDate: Date, endDate: Date): List<CategoryExpenseBreakdown> {
        val startEpoch = startDate.toEpochDay()
        val endEpoch = endDate.toEpochDay()

        val rows = arCategoryDao.getExpenseBreakdown(startEpoch, endEpoch)

        return rows.map(CategoryExpenseRow::toDomain)
    }
}