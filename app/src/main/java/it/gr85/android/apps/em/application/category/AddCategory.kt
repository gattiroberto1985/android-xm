package it.gr85.android.apps.em.application.category

import it.gr85.android.apps.em.application.services.ColorGenerator
import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.newId
import it.gr85.android.apps.em.domain.ports.CategoryRepository

data class AddCategoryCommand(
    val name: String,
    val color: Color?
)


class AddCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    private val colorGenerator: ColorGenerator
) {

    // consente l'utilizzo con solo addCategoryUseCase(category) !
    suspend operator fun invoke(
        command: AddCategoryCommand
    ): Category {

        if ( categoryRepository.getByName(command.name) != null ) {
            throw DuplicateCategoryException()
        }

        val name = command.name.trim()
        var color = command.color // can be null!

        if ( color == null ) {
            val usedColors = categoryRepository.getUsedColors()
            color = colorGenerator.generateUnusedColor(usedColors)
        }

        val category = Category(
            id = newId(),
            name = name,
            color = color
        )

        return categoryRepository.save(category)
    }
}
