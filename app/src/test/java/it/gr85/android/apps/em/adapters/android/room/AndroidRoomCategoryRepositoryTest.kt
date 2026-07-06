package it.gr85.android.apps.em.adapters.android.room

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.just
import io.mockk.mockk
import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Color
import it.gr85.android.apps.em.domain.model.Id
import it.gr85.android.apps.em.domain.model.newId
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class AndroidRoomCategoryRepositoryTest {

    private val __updatedTimestamp = 16042007L

    private val dao = mockk<ARCategoryDao>()
    private val cr = ARCategoryRepository(
        arCategoryDao = dao,
        now = { __updatedTimestamp }
    )

    @Before
    fun setup() {
    }

    @Test
    fun testGetUsedColors() = runTest {
        // Given
        coEvery { dao.getUsedColors() } returns setOf("#000000", "#111111")

        // When
        val result = cr.getUsedColors()

        // Then
        Assert.assertEquals(
            setOf("#000000", "#111111").map(Color::of).toSet(),
            result
        )

        coVerify(exactly = 1) {
            dao.getUsedColors()
        }

        confirmVerified(dao)

    }

    @Test
    fun `save should update if category is found`() = runTest {
        // Given
        val id : Id = newId()
        val c = Category(id, "aaa", Color.of( "#112233"))
        val ceExisting = ARCategoryEntity(
           id = id.toString(),
            name ="aaa",
            hexColor = "#112233",
            timestamps = Timestamps(
                100L, 200L
            )
        )

        coEvery { dao.findById( id.toString() ) } returns ceExisting
        // we need those lines because these are method potentially called, so mockk
        // wants all covered
        coEvery { dao.update(any()) } just Runs
        coEvery { dao.insert(any()) } just Runs

        // When
        val savedC = cr.save( c )

        // Then
        Assert.assertEquals(c, savedC)
        coVerify(exactly = 0) { dao.insert(any()) }

        coVerify(exactly = 1) {
            dao.update(match {
                it.id == id.toString() &&
                it.name == "aaa" &&
                it.timestamps.created_at == 100L &&
                it.timestamps.updated_at == __updatedTimestamp
            })
        }

        // to be verified the dao, every mock should be verified!
        coVerify(exactly = 1) {
            dao.findById(id.toString())
        }

        confirmVerified(dao)

    }

    @Test
    fun `save should insert if category is not found`() = runTest {
        // Given
        val id : Id = newId()
        val c = Category(id, "aaa", Color.of( "#112233"))

        coEvery { dao.findById( id.toString() ) } returns null
        // we need those lines because these are method potentially called, so mockk
        // wants all covered
        coEvery { dao.update(any()) } just Runs
        coEvery { dao.insert(any()) } just Runs

        // When
        val savedC = cr.save( c )

        // Then
        Assert.assertEquals(c, savedC)
        coVerify(exactly = 0) { dao.update(any()) }

        coVerify(exactly = 1) {
            dao.insert(match {
                it.id == id.toString() &&
                it.name == "aaa" &&
                it.timestamps.created_at == __updatedTimestamp &&
                it.timestamps.updated_at == __updatedTimestamp
            })
        }

        // to be verified the dao, every mock should be verified!
        coVerify(exactly = 1) {
            dao.findById(id.toString())
        }

        confirmVerified(dao)
    }

}