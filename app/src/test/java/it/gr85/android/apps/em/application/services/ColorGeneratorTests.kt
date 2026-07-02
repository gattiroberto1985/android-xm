package it.gr85.android.apps.em.application.services

import kotlinx.coroutines.test.runTest
import kotlin.test.assertFailsWith
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.application.exceptions.NoMoreAvailableColorsException
import kotlin.random.Random


// Test purpose class, in order to check that the DefaultColorGenerator pass seamlessly
// through the PaletteColorGenerator after color pool is exhausted
class SpyRandomColorGenerator : ColorGenerator {

    var called = false

    override fun generateUnusedColor(
        usedColors: Set<Color>
    ): Color {
        called = true
        return Color.of(
            "#%06X".format(
                Random.nextInt(0x1000000)
            )
        )
    }
}



class ColorGeneratorTests {

    @Before
    fun setup() { }

    @Test
    fun testPaletteColorGenerator() = runTest {

        val pcg = PaletteColorGenerator(
            listOf( "#F44336", "#E91E63").map(Color::of)
        )
        val usedColors: MutableSet<Color> = mutableSetOf()

        repeat(2) {
            usedColors.add(
                pcg.generateUnusedColor(usedColors)
            )
        }


        assertFailsWith<NoMoreAvailableColorsException> {
            pcg.generateUnusedColor(usedColors)
        }

    }

    @Test
    fun testRandomColorGenerator() = runTest {
        val rcg = RandomColorGenerator()
        val usedColors = listOf( "#F44336", "#E91E63").map(Color::of).toSet()

        val newColor = rcg.generateUnusedColor(usedColors)

        Assert.assertFalse(usedColors.contains(newColor))
        Assert.assertEquals(7, newColor.value.length)
        // More test conditions should be placed!
    }


    @Test
    fun testDefaultColorGenerator() = runTest {
        val srcg = SpyRandomColorGenerator()
        val pcg = PaletteColorGenerator(
            listOf( "#F44336", "#E91E63").map(Color::of)
        )

        val dcg = DefaultColorGenerator( pcg, srcg )

        val usedColors : MutableSet<Color> = mutableSetOf()

        repeat(2) {
            usedColors.add(
                dcg.generateUnusedColor(usedColors)
            )
        }

        // no exception should be thrown, random color generator should be invoked!
        // These two tests check the workflow from external as a black box
        usedColors.add( dcg.generateUnusedColor(usedColors) )
        usedColors.add( dcg.generateUnusedColor(usedColors) )

        // Those two are specifically aimed to check that, once the pool of
        // colors in the palette are exhausted, the second generator will be
        // called
        Assert.assertEquals( srcg.called, true )
        Assert.assertEquals( usedColors.size, 4 )
    }

}