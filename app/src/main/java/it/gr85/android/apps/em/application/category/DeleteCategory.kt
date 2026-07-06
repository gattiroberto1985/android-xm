package it.gr85.android.apps.em.application.category

import it.gr85.android.apps.em.application.services.ColorGenerator
import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.Constants
import it.gr85.android.apps.em.domain.model.newId
import it.gr85.android.apps.em.domain.ports.CategoryRepository
import it.gr85.android.apps.em.domain.ports.TransactionRepository

data class DeleteCategoryCommand(
    val name: String
)


class DeleteCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    private val transactionsRepository: TransactionRepository
) {

    // consente l'utilizzo con solo addCategoryUseCase(category) !
    suspend operator fun invoke(
        command: DeleteCategoryCommand
    ): Category {

        val categoryToDelete = categoryRepository.getByName(command.name) ?: throw CategoryNotFoundException()

        // This is generally fine, for academic purposes... but what if, say, a migration went wrong
        // and the default category is not found? In this case an exception will be thrown but no
        // info are provided to the caller
        //val defaultCategory = categoryRepository.getByName(DEFAULT_CATEGORY_NAME)!! // double ! implies we expect not null!
        val defaultCategory = categoryRepository.getByName(Constants.DEFAULT_CATEGORY_NAME) ?: throw CategoryNotFoundException()

        /*
        // 01. The basic way: not so much efficient: an UPDATE statement FOR EACH transaction... not very good design, isn't it?
        // Assign category-orphan transactions to default category OTHER

        // Get transaction by category
        val orphanedTransactions = transactionsRepository.getByCategory( categoryToDelete )

        for ( orphanedTransaction in orphanedTransactions ) {
            val reassignedTransaction = orphanedTransaction.copy(
                category = defaultCategory
            )
            transactionsRepository.updateCategoryFor( reassignedTransaction )
        }

        // 02. One update query only (but we still need to query the database, before!):
            transactionsRepository.updateCategoryForAll(
            transactionIds = orphanedTransactions.map { it.id },
            newCategory = defaultCategory
        )
        */

        // 03. The RIGHT way: let db do all. One single query to UPDATE with WHERE clause
        transactionsRepository.reassignTransactionsByCategory(
            fromCategory = categoryToDelete,
            toCategory = defaultCategory
        )

        // delete the category
        categoryRepository.delete( categoryToDelete.id )
        
        return categoryToDelete
    }
}
