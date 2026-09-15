package it.gr85.android.apps.em.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import it.gr85.android.apps.em.application.AppContainer
import it.gr85.android.apps.em.ui.home.HomeScreen
import it.gr85.android.apps.em.ui.home.HomeUiViewModel

/**
 * MainActivity
 *
 * Entry point dell'app.
 * Orchestrazione di:
 * 1. SplashScreen API (Material Design 3)
 * 2. AppContainer (DI)
 * 3. Navigation root (Compose)
 */
class MainActivity : ComponentActivity() {

    // ViewModel della home screen (inizializzato dopo il setup)
    private var homeViewModel: HomeUiViewModel? = null

    // Flag che tiene traccia se l'app è stata inizializzata
    private var isInitializing by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ===== STEP 1: Setup SplashScreen API =====
        val splashScreen = installSplashScreen() // Deve essere prima di setContent()

        // Configura la condition: mostra lo splash finché isInitializing è true
        splashScreen.setKeepOnScreenCondition {
            isInitializing
        }

        // ===== STEP 2: Inizializza AppContainer e ViewModel =====
        // Esecuzione in background, la SplashScreen rimane visibile
        val appContainer = AppContainer(applicationContext)
        homeViewModel = appContainer.createHomeUiViewModel()

        // Quando finito, segnala che l'app è pronta
        isInitializing = false

        // ===== STEP 3: Setup composable content =====
        setContent {
            AppTheme {
                // Navigation root
                NavigationRoot(
                    homeViewModel = homeViewModel!!,
                    onNavigateToCategoryDetail = { categoryId ->
                        // TODO: implementa navigazione a CategoryDetailScreen
                        println("Navigate to category: $categoryId")
                    },
                    onNavigateToSettings = {
                        // TODO: implementa navigazione a SettingsScreen
                        println("Navigate to settings")
                    },
                    onNavigateToSearch = {
                        // TODO: implementa navigazione a SearchScreen
                        println("Navigate to search")
                    }
                )
            }
        }
    }
}

/**
 * NavigationRoot
 *
 * Composable root per la navigazione dell'app.
 * Per adesso mostra solo HomeScreen.
 * Quando avrai altre screen, implementerai NavController/NavGraph qui.
 */
@androidx.compose.runtime.Composable
private fun NavigationRoot(
    homeViewModel: HomeUiViewModel,
    onNavigateToCategoryDetail: (categoryId: String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    // TODO: Implementare NavController e NavGraph per navigazione fra screen
    // Per adesso, mostra solo HomeScreen

    HomeScreen(
        viewModel = homeViewModel,
        onNavigateToCategoryDetail = onNavigateToCategoryDetail,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToSearch = onNavigateToSearch
    )
}