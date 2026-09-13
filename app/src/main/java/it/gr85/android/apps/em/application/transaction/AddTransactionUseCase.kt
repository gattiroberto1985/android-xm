package it.gr85.android.apps.em.application.transaction

import it.gr85.android.apps.em.domain.model.Transaction
import it.gr85.android.apps.em.domain.model.MovementType
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.idFrom
import it.gr85.android.apps.em.domain.model.newId
import it.gr85.android.apps.em.domain.ports.CategoryRepository
import it.gr85.android.apps.em.domain.ports.TransactionRepository
import java.util.UUID

/**
 * Command per AddTransactionUseCase.
 * Incapsula i dati necessari all'inserimento di una nuova transazione.
 */
data class AddTransactionCommand(
    val description: String,
    val amount: Long,  // in centesimi
    val categoryId: String,  // ID della categoria
    val movementType: MovementType,
    val date: Date = Date.now()  // default: oggi
)

/**
 * AddTransactionUseCase
 *
 * Responsabilità:
 * - Validazione dell'input (amount > 0, description non vuota, ecc.)
 * - Creazione del domain object Transaction
 * - Persistenza via repository
 * - Gestione degli errori
 *
 * Emette Result.Success(Unit) se l'insert riesce,
 * Result.Error se c'è un problema (validazione o persistenza)
 */
class AddTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(command: AddTransactionCommand): Result<Unit> {

        val validationError = validateCommand(command)
        if (validationError != null) {
            return Result.failure(validationError)
        }

        val category =
            categoryRepository.getById(idFrom(command.categoryId)) ?: return Result.failure(
                IllegalArgumentException("Categoria non trovata")
            )

        val transaction = try {
            Transaction(
                id = newId(),
                amount = command.amount,
                type = command.movementType,
                category = category,
                description = command.description,
                date = command.date
            )
        } catch (e: Exception) {
            return Result.failure(
                Exception("Errore nella creazione della transazione: ${e.message}", e)
            )
        }

        // ===== PERSISTENZA =====
        return try {
            transactionRepository.save(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                Exception("Errore durante il salvataggio: ${e.message}", e)
            )
        }
    }

    // ===== PRIVATE HELPERS =====

    /**
     * Valida il comando.
     * Ritorna un'Exception se c'è un errore di validazione, null altrimenti.
     */
    private fun validateCommand(command: AddTransactionCommand): Exception? {
        // Validazione: description
        if (command.description.isBlank()) {
            return IllegalArgumentException("La descrizione non può essere vuota")
        }

        // Validazione: amount
        if (command.amount <= 0L) {
            return IllegalArgumentException("L'importo deve essere > 0")
        }

        // Validazione: categoryId
        if (command.categoryId.isBlank()) {
            return IllegalArgumentException("La categoria non può essere vuota")
        }

        // Se tutto è valido
        return null
    }

    /**
     * Genera un ID univoco per il Transaction.
     * Nel tuo caso usi UUID (come suggerito dalle tue type aliases).
     */
    private fun generateMovementoId(): String = UUID.randomUUID().toString()
}
