package it.gr85.android.apps.em.ui.examples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun NoLayout() {
    // sono sovrapposti!!

    Text("one")
    Text("twoooooooo")
    Column {

        Row {
            Text("three")
            Text("four")
        }

        Column() {
            Text("five")
            Text("six")
        }
    }
}