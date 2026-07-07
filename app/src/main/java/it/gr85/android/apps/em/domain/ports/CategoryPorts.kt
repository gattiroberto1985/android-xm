package it.gr85.android.apps.em.domain.ports

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.Id

interface CategoryRepository {

    suspend fun getById( id: Id): Category?

    suspend fun getByName( name: String): Category?

    suspend fun getAll( ) : List<Category>
    suspend fun getUsedColors(): Set<Color>

    suspend fun search ( name: String): List<Category>

    suspend fun save( category: Category): Category

    suspend fun delete( id: Id )

    suspend fun getExpenseBreakdown( startDate: Date, endDate: Date): List<CategoryExpenseBreakdown>
}
