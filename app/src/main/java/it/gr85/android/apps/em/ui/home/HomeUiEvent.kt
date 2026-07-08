package it.gr85.android.apps.em.ui.home

import it.gr85.android.apps.em.domain.model.Date

// eventi EMESSI dalla UI! non eventi "di risposta" da view model a UI!
sealed class HomeUiEvent {
    data class OnDateRangeChanged(val startDate: Date, val endDate: Date ) : HomeUiEvent()

    data class OnCategoryTap(val categoryId: String) : HomeUiEvent()

    object OnLegendToggle : HomeUiEvent()

    object OnSettingsClick : HomeUiEvent()

    object OnSearchClick : HomeUiEvent()

    object OnRetryClick : HomeUiEvent()
}