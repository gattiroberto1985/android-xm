package it.gr85.android.apps.em.application.category

import it.gr85.android.apps.em.application.services.PaletteColorGenerator
import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.Id
import it.gr85.android.apps.em.domain.ports.CategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class CategoryRepositoryFixture : CategoryRepository {
    override suspend fun getById(id: Id): Category? { return null }

    override suspend fun getByName(name: String): Category? {
        if( name == "expected_existing_category") {
            throw DuplicateCategoryException()
        }
        else return null
    }

    override suspend fun getAll(): List<Category> { return listOf() }

    override suspend fun getUsedColors(): Set<Color> { return setOf() }

    override suspend fun search(name: String): List<Category> { return listOf() }

    override suspend fun save(category: Category): Category { return category }

    override suspend fun delete(id: Id) { }
    override suspend fun getExpenseBreakdown(
        startDate: Date,
        endDate: Date
    ): List<CategoryExpenseBreakdown> {
        TODO("Not yet implemented")
    }

}

class AddCategoryTest {

    private val cr  = CategoryRepositoryFixture()

    @Before
    fun setup() { }

    @Test
    fun `invoke should throw duplicate category exception if category exists`() = runTest {
        // Given
        // When
        assertFailsWith<DuplicateCategoryException> {
            AddCategoryUseCase(cr, PaletteColorGenerator()).invoke(
                AddCategoryCommand( "expected_existing_category", null )
            )
        }
    }

    @Test
    fun `invoke should add a color if not provided and trim name`() = runTest {
        // Given
        val name = "   new_category   "
        val color = null

        // When
        val addedCategory = AddCategoryUseCase(
            cr, PaletteColorGenerator()
        ).invoke(
            AddCategoryCommand( name, null )
        )

        // Then
        Assert.assertEquals( addedCategory.name, "new_category")
        Assert.assertNotNull( addedCategory.color )
    }

    @Test
    fun `invoke should pass through normally if all setup`() = runTest {

        // Given
        val name = "aaa"
        val color = Color.of("#998877")

        // When
        val addedCategory = AddCategoryUseCase(
        cr, PaletteColorGenerator()
            ).invoke(
        AddCategoryCommand( name, color )
        )

        // Then
        Assert.assertEquals( addedCategory.name, name)
        Assert.assertEquals( addedCategory.color, color)
        Assert.assertNotNull( addedCategory.id)

    }
}