package it.gr85.android.apps.em.domain.ports

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Id

interface CategoryRepository {

    suspend fun getById( id: Id): Category?
    suspend fun getAll( ) : List<Category>

    suspend fun search ( name: String): List<Category>

    suspend fun save( category: Category): Category

    suspend fun delete( id: Id )

}
