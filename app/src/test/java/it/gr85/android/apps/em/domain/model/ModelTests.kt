@file:OptIn(ExperimentalTime::class)

package it.gr85.android.apps.em.domain.model

import org.junit.Before
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.time.ExperimentalTime


class ModelTests {

    @Before
    fun setup() { }

    @Test
    fun testId() {
        val uuidString = UUID.randomUUID().toString()
        val id = newId()
        // TODO: assert is an uuid
        val fromStringId = idFrom( uuidString )
        // TODO: assert is the right value!
    }

    @Test
    fun testCategory() {
        val c1 = Category( newId(), "category 1", "#aabbcc")
        //assert( c1.id != null)
        assert( c1.name.equals("category 1"))
        assert( c1.colorHexCode.equals("#aabbcc"))
    }


    @Test
    fun testMovement() {
        val now = Date.now()
        val category = Category( newId(), "cat1", "#ffeedd")
        val m1 = Transaction(
            newId(),
            MovementType.INCOME,
            8.34,
            now,
            "entrata di test",
            category)

        var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")

        assert( m1.date.format( formatter ).equals( now.format( formatter ) ) )
        assert( m1.amount == 8.34 )
    }
}
