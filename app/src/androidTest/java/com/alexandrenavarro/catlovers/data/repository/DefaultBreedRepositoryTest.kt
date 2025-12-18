package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedImage
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultBreedRepositoryTest {

    private lateinit var breedRemoteDataSource: BreedRemoteDataSource
    private lateinit var breedDataBase: BreedsDatabase

    @Before
    fun setup() {
        breedDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            BreedsDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        breedDataBase.close()
    }

    @Test
    fun givenRepositoryJustCreatedWhenObservingBreedsThenEmitsEmptyList() = runTest {
        breedRemoteDataSource = FakeBreedRemoteDataSource(Result.Success(emptyList()))

        val sut = DefaultBreedRepository(
            breedRemoteDataSource = breedRemoteDataSource,
            breedDataBase = breedDataBase,
        )

        val items = sut.getBreeds().asSnapshot {}

        assertEquals(0, items.size)
    }

    @Test
    fun givenObservingBreedsWhenDatabaseIsEmptyThenShouldFetchFromRemoteAndEmit() = runTest {
        breedRemoteDataSource = FakeBreedRemoteDataSource(
            Result.Success(
                listOf(
                    NetworkBreedPreview(
                        "3",
                        "name",
                        image = NetworkBreedImage(
                            id = "id",
                            imageUrl = "url"
                        )
                    ),
                    NetworkBreedPreview(
                        "1",
                        "test",
                        image = NetworkBreedImage(
                            id = "5ssss",
                            imageUrl = "url2"
                        )
                    )
                )
            )
        )

        val sut = DefaultBreedRepository(
            breedRemoteDataSource = breedRemoteDataSource,
            breedDataBase = breedDataBase,
        )

        val items = sut.getBreeds().asSnapshot()
        assertEquals(2, items.size)
        assertEquals("3", items.first().id)
        assertEquals("name", items.first().name)
        assertEquals("url", items.first().imageUrl)
        assertEquals("id", items.first().imageId)
    }

    @Test
    fun givenGetBreedsWhenRemoteReturnsErrorThenShouldEmitError() = runTest {
        breedRemoteDataSource =
            FakeBreedRemoteDataSource(Result.Error(Exception("Connection error")))

        val sut = DefaultBreedRepository(
            breedRemoteDataSource = breedRemoteDataSource,
            breedDataBase = breedDataBase,
        )

        try {
            sut.getBreeds().asSnapshot()
            fail("The Snapshot should be thrown")
        } catch (e: Exception) {
            assertEquals("Connection error", e.message)
        }
    }
}