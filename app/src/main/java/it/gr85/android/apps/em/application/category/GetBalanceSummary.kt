package it.gr85.android.apps.em.application.category

import it.gr85.android.apps.em.application.exceptions.InvalidDateRangeException
import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.ports.CategoryRepository
import it.gr85.android.apps.em.domain.ports.TransactionRepository

data class BalanceSummaryCommand(
    val from: Date?,
    val to: Date?
)

class GetBalanceSummary (
    val transactionRepository: TransactionRepository,
    ) {
    suspend operator fun invoke(
        command: BalanceSummaryCommand
    ): Long {

        if ( command.from == null || command.to == null ) {
            throw InvalidDateRangeException()
        }

        return transactionRepository.getBalanceSummary(command.from, command.to)
    }
}