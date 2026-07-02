package it.gr85.android.apps.em.application.services

import it.gr85.android.apps.em.application.exceptions.NoMoreAvailableColorsException
import it.gr85.android.apps.em.domain.model.Color
import kotlin.random.Random


interface ColorGenerator {
    fun generateUnusedColor(usedColors : Set<Color>): Color
}

// TODO: extract and manage with external properties file!
val defaultPalette = listOf(
"#F44336",
"#E91E63",
"#9C27B0",
"#673AB7",
"#3F51B5",
"#2196F3",
"#03A9F4",
"#00BCD4",
"#009688",
"#4CAF50",
"#8BC34A",
"#CDDC39",
"#FFC107",
"#FF9800",
"#FF5722"
).map(Color::of)


class PaletteColorGenerator(
    private val palette: List<Color> = defaultPalette
) : ColorGenerator {

    override fun generateUnusedColor(usedColors : Set<Color>): Color {

        return palette.firstOrNull {
            it !in usedColors
        } ?: throw NoMoreAvailableColorsException()
    }

}


class RandomColorGenerator : ColorGenerator {
    override fun generateUnusedColor(usedColors : Set<Color>): Color {
        while (true) {
            val candidate =
                Color.of(
                    "#%06X".format(
                        Random.nextInt(0x1000000)
                    )
                )
            if (candidate !in usedColors) {
                return candidate
            }
        }

    }
}



class DefaultColorGenerator(
    private val paletteGenerator: ColorGenerator = PaletteColorGenerator(),
    private val randomGenerator: ColorGenerator = RandomColorGenerator()
) : ColorGenerator {

    override fun generateUnusedColor(
        usedColors: Set<Color>
    ): Color {

        return try {

            paletteGenerator.generateUnusedColor(
                usedColors
            )

        } catch (_: NoMoreAvailableColorsException) {

            randomGenerator.generateUnusedColor(
                usedColors
            )

        }
    }
}
