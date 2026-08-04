package it.gr85.android.apps.em.ui.examples

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun Sample01() {
    Text(
        text = "Hello from compose!",
        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
        color = Color.Cyan,
        fontSize = 20.sp,
        fontWeight = FontWeight.Thin,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        maxLines = 2,
        softWrap = true,
        letterSpacing = 2.sp,
    )
}


@Preview
@Composable
fun DisplayImage() {
    Image(
        painter = androidx.compose.ui.res.painterResource(id = it.gr85.android.apps.em.R.drawable.ic_launcher_foreground),
        contentDescription = "Sample Image",
        alignment = androidx.compose.ui.Alignment.TopStart,
        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
        alpha = 0.5f, // transparency
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Red), // tint color
        modifier = Modifier
            .size(100.dp)
            .padding(16.dp)
            // Clip image to be shaped as a circle
            .clip(CircleShape)
    )
}

@Preview
@Composable
fun ButtonSample() {
    androidx.compose.material3.Button(
        onClick = { /* Handle button click */ },
        modifier = Modifier.padding(16.dp),
        shape = RectangleShape,
        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp,
            disabledElevation = 0.dp
        ),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.Blue,
            contentColor = Color.White
        )
    ) {
        Text(text = "Click Me")
    }
}

@Preview
@Composable
fun TextField() {

}