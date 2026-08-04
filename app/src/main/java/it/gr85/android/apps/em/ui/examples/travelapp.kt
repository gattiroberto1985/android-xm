package it.gr85.android.apps.em.ui.examples

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.gr85.android.apps.em.R

@Preview
@Composable
fun TravelApp() {
    // Implement the UI for the travel app here

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column() {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Travel Image",
                    contentScale = ContentScale.Crop,
                )

                Icon(
                    imageVector = Icons.Default.Favorite,
                    tint = Color.Red,
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                    contentDescription = "Favorite",

                    )
            }
            Text(
                text = "Bali, Thailandia",
                modifier = Modifier.padding(8.dp),
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "A tropical paradise wit stunning beaches, temples and vibrant culture",
                modifier = Modifier.padding(8.dp),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )

            Row {

                Button(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
                ) {
                    Text(text = "Book Now")
                }
            }
        }
    }

}