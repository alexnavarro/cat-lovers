package com.alexandrenavarro.catlovers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
import com.alexandrenavarro.catlovers.data.network.CatBreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedImage
import com.alexandrenavarro.catlovers.data.network.model.NetworkCatBreedPreview
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalPagingApi::class)
class BreedRemoteMediatorTest {

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
    fun givenEmptyDatabaseWhenRefreshThenDataIsInserted() = runTest {
        catBreedRemoteDataSource = FakeCatBreedRemoteDataSource(
            Result.Success(
                listOf(
                    NetworkCatBreedPreview(
                        id = "3",
                        name = "Abyssinian",
                        image = NetworkBreedImage(
                            id = "id",
                            imageUrl = "url"
                        ),
                        lifeSpan = "14 - 15"
                    ),
                    NetworkCatBreedPreview(
                        id = "4",
                        name = "Test",
                        image = NetworkBreedImage(
                            id = "id2",
                            imageUrl = "url2"
                        ),
                        lifeSpan = "14 - 24"
                    )
                )
            )
        )

        val sut = CatBreedRemoteMediator(catBreedRemoteDataSource, breedDataBase)

        val result = sut.load(
            LoadType.REFRESH,
            PagingState(emptyList(), null, PagingConfig(20), 0)
        )

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(2, breedDataBase.catBreedsDao().count())
    }

    @Test
    fun givenEmptyDatabaseWhenRefreshAndResultIsEmptyThenDataIsNotInserted() = runTest {
        catBreedRemoteDataSource = FakeCatBreedRemoteDataSource(Result.Success(emptyList()))

        val sut = CatBreedRemoteMediator(catBreedRemoteDataSource, breedDataBase)

        val result = sut.load(
            LoadType.REFRESH,
            PagingState(emptyList(), null, PagingConfig(20), 0)
        )

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(0, breedDataBase.catBreedsDao().count())
    }

    @Test
    fun givenEmptyDatabaseWhenRefreshAndResultIsErrorThenResultIsError() = runTest {
        catBreedRemoteDataSource = FakeCatBreedRemoteDataSource(Result.Error(Exception()))

        val sut = CatBreedRemoteMediator(catBreedRemoteDataSource, breedDataBase)

        val result = sut.load(
            LoadType.REFRESH,
            PagingState(emptyList(), null, PagingConfig(20), 0)
        )

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(0, breedDataBase.catBreedsDao().count())
    }
}