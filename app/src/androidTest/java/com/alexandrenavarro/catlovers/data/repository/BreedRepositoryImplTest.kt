package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
import com.alexandrenavarro.catlovers.data.network.CatBreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedImage
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedPreview
import com.alexandrenavarro.catlovers.domain.model.CatBreedDetail
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class BreedRepositoryImplTest {

    private lateinit var catBreedRemoteDataSource: CatBreedRemoteDataSource
    private lateinit var breedDataBase: CatBreedsDatabase

    @Before
    fun setup() {
        breedDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CatBreedsDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        breedDataBase.close()
    }

    @Test
    fun givenRepositoryJustCreatedWhenObservingBreedsThenEmitsEmptyList() = runTest {
        catBreedRemoteDataSource = FakeCatBreedRemoteDataSource(Result.Success(emptyList()))

        val sut = CatBreedRepositoryImpl(
            catBreedRemoteDataSource = catBreedRemoteDataSource,
            breedDataBase = breedDataBase,
        )

        val items = sut.getCatBreeds(null).asSnapshot {}

        assertEquals(0, items.size)
    }

    @Test
    fun givenObservingBreedsWhenDatabaseIsEmptyThenShouldFetchFromRemoteAndEmit() = runTest {
        catBreedRemoteDataSource = FakeCatBreedRemoteDataSource(
            Result.Success(
                listOf(
                    NetworkCatBreedPreview(
                        "3",
                        "name",
                        image = NetworkBreedImage(
                            id = "id",
                            imageUrl = "url"
                        ),
                        lifeSpan = "14 - 15"
                    ),
                    NetworkCatBreedPreview(
                        "1",
                        "test",
                        image = NetworkBreedImage(
                            id = "5ssss",
                            imageUrl = "url2"
                        ),
                        lifeSpan = "14 - 24"
                    )
                )
            )
        )

        val sut = CatBreedRepositoryImpl(
            catBreedRemoteDataSource = catBreedRemoteDataSource,
            breedDataBase = breedDataBase,
        )

        val items = sut.getCatBreeds(null).asSnapshot()
        assertEquals(2, items.size)
        assertEquals("3", items.first().id)
        assertEquals("name", items.first().name)
        assertEquals("url", items.first().imageUrl)
        assertEquals("id", items.first().imageId)
    }

    @Test
    fun givenGetBreedsWhenRemoteReturnsErrorThenShouldEmitError() = runTest {
        catBreedRemoteDataSource =
            FakeCatBreedRemoteDataSource(Result.Error(Exception("Connection error")))

        val sut = CatBreedRepositoryImpl(
            catBreedRemoteDataSource = catBreedRemoteDataSource,
            breedDataBase = breedDataBase,
        )

        try {
            sut.getCatBreeds(null).asSnapshot()
            fail("The Snapshot should be thrown")
        } catch (e: Exception) {
            assertEquals("Connection error", e.message)
        }
    }

    @Test
    fun givenFetchBreedWhenRemoteReturnsSuccessThenRepositoryReturnsSuccess() = runTest {
        val networkDetail = NetworkCatBreedDetail(
            id = "1",
            name = "name",
            description = "description",
            temperament = "temperament",
            origin = "Portugal",
            imageId = "imageId"
        )

        val remote = FakeCatBreedRemoteDataSource(breedDetail=Result.Success(networkDetail))

        val sut = CatBreedRepositoryImpl(
            catBreedRemoteDataSource = remote,
            breedDataBase = breedDataBase
        )

        val result = sut.getCatBreedDetail("1")
        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertTrue(data is CatBreedDetail)
    }

    @Test
    fun givenFetchBreedWhenRemoteReturnsErrorThenRepositoryReturnsErrorWithSameMessage() = runTest {
        val ex = Exception("Connection error")
        val remote = FakeCatBreedRemoteDataSource(breedDetail=Result.Error(ex))

        val sut = CatBreedRepositoryImpl(
            catBreedRemoteDataSource = remote,
            breedDataBase = breedDataBase
        )

        val result = sut.getCatBreedDetail("1")
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertEquals("Connection error", error.exception.message)
    }

    @Test
    fun givenFetchBreedWhenRemoteReturnsNetworkErrorThenRepositoryReturnsNetworkError() = runTest {
        val ioEx = IOException("io")
        val remote = FakeCatBreedRemoteDataSource(breedDetail=Result.NetworkError(ioEx))

        val sut = CatBreedRepositoryImpl(
            catBreedRemoteDataSource = remote,
            breedDataBase = breedDataBase
        )

        val result = sut.getCatBreedDetail("1")
        assertTrue(result is Result.NetworkError)
        val netErr = result as Result.NetworkError
        assertEquals("io", netErr.exception.message)
    }

}