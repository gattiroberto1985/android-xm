package it.gr85.android.apps.em

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import it.gr85.android.apps.em.ui.AppTheme
import it.gr85.android.apps.em.ui.home.HomeScreen
import it.gr85.android.apps.em.ui.home.HomeScreenPreview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Temporary placeholder, sarà sostituito con la navigation
                    HomeScreenPreview(
                        onDateRangeSelected = { start, end ->
                            Log.i("EMBOB", "Date range selected: $start to $end")
                        }
                    )
                }
            }
        }
    }
}