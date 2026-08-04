package it.gr85.android.apps.em.ui.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }
    var localCounter = 0

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).background(Color.Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Count: $count")
        Text(text = "LocalCounter: $localCounter")
        Spacer(modifier = Modifier.height(16.dp))
        // count will be preserved, localcounter not!
        Button(onClick = { count++ ; localCounter++}) {
            Text(text = "Increment")
        }
    }
}