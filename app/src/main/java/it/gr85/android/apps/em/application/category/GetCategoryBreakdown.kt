package it.gr85.android.apps.em.application.category

import it.gr85.android.apps.em.application.exceptions.InvalidDateRangeException
import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.ports.CategoryRepository

data class CategoryBreakdownCommand(
    val from: Date?,
    val to: Date?
)

class GetCategoryBreakdown (
    val categoryRepository: CategoryRepository,

) {
    suspend operator fun invoke(
        command: CategoryBreakdownCommand
    ): List<CategoryExpenseBreakdown> {

        if ( command.from == null || command.to == null ) {
            throw InvalidDateRangeException()
        }

        return categoryRepository.getExpenseBreakdown(command.from, command.to)
    }
}