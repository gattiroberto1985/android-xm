package it.gr85.android.apps.em.ui.home

import android.graphics.Color
import android.graphics.Color.parseColor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gr85.android.apps.em.application.category.BalanceSummaryCommand
import it.gr85.android.apps.em.application.category.CategoryBreakdownCommand
import it.gr85.android.apps.em.application.category.GetBalanceSummary
import it.gr85.android.apps.em.application.category.GetCategoryBreakdown
import it.gr85.android.apps.em.application.exceptions.InvalidDateRangeException
import it.gr85.android.apps.em.domain.model.CategoryExpenseBreakdown
import it.gr85.android.apps.em.domain.model.Date
import it.gr85.android.apps.em.domain.model.DateRange
import it.gr85.android.apps.em.ui.model.CategoryExpenseBreakdownUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalCoroutinesApi::class)
class HomeUiViewModel(
    private val getCategoryBreakdown: GetCategoryBreakdown,
    private val getBalanceSummary: GetBalanceSummary
) : ViewModel() {

    // stati privati (solo il viewmodel modifica)
    private val _uiState = MutableStateFlow(HomeUiState())
    // solo il view model emette eventi
    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    // variabile di appoggio per il segnale di invalidazione dei dati, che quando emesso forza
    // il reload
    private val _invalidateSignal = MutableSharedFlow<Unit>()

    // region PROPERTIES
    // per esporre alla UI lo state e quindi popolare con i dati l'interfaccia
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    // per fare in modo che la UI possa ricevere notifica degli eventi
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    init {
        // impostiamo il segnale di reset. È l'unico punto in cui impostiamo cosa deve succedere
        // quando carichiamo i dati, evitando quindi di continuare a scrivere un loadHomeData() ogni
        // volta che c'è bisogno.
        // Queste istruzioni significano:
        // - ogni volta che viene emesso un evento su _invalidateSignal
        _invalidateSignal
            // ... fai quanto indicato dentro flatMapLatest (flatten + map + latest,
            // cioè "appiattisci" gli eventi, tieni solo l'ultimo cancellando eventuali
            // precedenti ancora in corso ed esegui (map) quanto indicato nel blocco { }
            .flatMapLatest { unitSignal ->
                println("🔄 flatMapLatest: ricevuto segnale, avvio loadHomeData()")
                // Ritorna un Flow che emette i dati necessari al modello
                loadHomeData()
            }
            // Per ogni elemento emesso da loadHomeData(), fai questo
            .onEach { (breakdown, balance ) ->
                println("✅ onEach: ricevuto (breakdown, balance), aggiorno state")
                updateStateWithData(breakdown, balance)
            }
            // AVVIO: Lancia il tutto nel scope specificato
            .launchIn(viewModelScope)
    }


    // region UTILITIES

    private suspend fun loadHomeData() = flow {

        try {
            val cebCommand = CategoryBreakdownCommand(
                _uiState.value.dateRange.start, _uiState.value.dateRange.end
            )

            val bsCommand = BalanceSummaryCommand(
                _uiState.value.dateRange.start, _uiState.value.dateRange.end
            )

            val balanceForRange: Long = getBalanceSummary.invoke(bsCommand)
            val categoryBreakdown = getCategoryBreakdown.invoke(cebCommand)

            // Emette il risultato
            emit(categoryBreakdown to balanceForRange)

        } catch (e: InvalidDateRangeException) {
            // Gestisci errore specifico
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Intervallo di date non valido."
            )
        } catch (e: Exception) {
            // Gestisci errore generico
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Errore sconosciuto."
            )
        }
    }

    private fun updateStateWithData(
        breakdown: List<CategoryExpenseBreakdown>,
        balance: Long
    ) {
        // Calcola la somma totale del breakdown
        val totalAmount = breakdown.sumOf { it.totalAmount }

        // Trasforma dominio in UI model
        val uiBreakdown = breakdown.map { expense ->
            val percentage = if (totalAmount > 0) {
                (expense.totalAmount.toFloat() / totalAmount) * 100f
            } else {
                0f
            }

            CategoryExpenseBreakdownUi(
                categoryId = expense.category.id.toString(),
                categoryName = expense.category.name,
                categoryColorArgb = expense.category.color.toString().toColorInt(),
                totalAmount = expense.totalAmount,
                percentageOfTotal = percentage,
                transactionCount = expense.transactionCount
            )
        }

        // Determina se il breakdown è vuoto
        val isEmpty = uiBreakdown.isEmpty()

        // Aggiorna lo state
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            balance = balance,
            categoryExpensesBreakdown = uiBreakdown,
            error = null
        )
    }

    // endregion UTILITIES


    // region EVENT HANDLER
    fun onDateRangeChanged(from: Date, to: Date) {
        if ( from > to ) {
            // NON si deve fare _uiState.value.error = "..." perchè con questa istruzione
            // NON vado a modificare il reference _uiState... cambio i valori al suo interno
            // ma il reference resta identico, quindi non verrebbero notificati gli osservatori
            // di questo cambio! Da qui la necessità del .copy()
            _uiState.value = _uiState.value.copy(
                error = "Data start non può essere successiva a data end!"
            )
            return
        }
        // possiamo andare ad aggiornare il datarange!
        // Aggiorna il dateRange nello state
        _uiState.value = _uiState.value.copy(
            dateRange = DateRange(from, to),
            isLoading = true,
            error = null
        )

        // ora bisogna dire che i dati sono invalidi e ritriggerare il reload! Tuttavia NON è un
        // evento che il view model deve passare all'interfaccia, c'è "solo" bisogno di invalidare
        // i dati. Questo farà in modo che il view model ri-faccia partire il caricamento e lo state
        // venga aggiornato, e la UI di conseguenza si aggiorna (perchè osserva il view model)
        viewModelScope.launch {
            _invalidateSignal.emit(Unit)  // Segnale interno, vedi
        }

    }

    fun onCategoryTap() { }

    fun onLegendToggle() { }

    fun onSettingsTap() { }

    fun onSearchTap() { }

    fun onRetry() { }

    // endregion EVENT HANDLER
}