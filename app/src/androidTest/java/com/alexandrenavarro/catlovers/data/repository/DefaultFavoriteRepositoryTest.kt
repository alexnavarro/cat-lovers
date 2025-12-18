package com.alexandrenavarro.catlovers.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alexandrenavarro.catlovers.data.database.BreedsDatabase
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity
import com.alexandrenavarro.catlovers.data.network.Result
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


class DefaultFavoriteRepositoryTest {
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
    fun givenAddFavoriteWhenRemoteReturnSuccessInsertsFavoritesAndReturnsSuccess() = runTest {
        val favoriteResult = Result.Success(42L)
        val favoriteRemote =
            FakeFavoriteRemoteDataSource(favoriteResult = favoriteResult, Result.Success(Unit))

        val sut = DefaultFavoriteRepository(
            favoriteRemoteDataSource = favoriteRemote,
            favoriteDao = breedDataBase.favoriteDao()
        )

        val result = sut.addFavorite("image-1")
        val dbFavorite = breedDataBase.favoriteDao().findFavoriteByImageId("image-1")

        assertTrue(result is Result.Success)
        assertNotNull(dbFavorite)
        assertEquals(42L, dbFavorite!!.id)
        assertEquals("image-1", dbFavorite.imageId)
    }

    @Test
    fun givenAddFavoriteWhenRemoteReturnsErrorThenReturnsErrorAndDoesNotInsert() = runTest {
        val favoriteRemote = FakeFavoriteRemoteDataSource(
            favoriteResult = Result.Error(Exception("Remote error")),
            Result.Error(Exception("Remote error"))
        )

        val sut = DefaultFavoriteRepository(
            favoriteRemoteDataSource = favoriteRemote,
            favoriteDao = breedDataBase.favoriteDao()
        )

        val result = sut.addFavorite("image-2")
        val dbFavorite = breedDataBase.favoriteDao().findFavoriteByImageId("image-2")

        assertTrue(result is Result.Error)
        assertEquals("Remote error", (result as Result.Error).exception.message)
        assertTrue(dbFavorite == null)
    }

    @Test
    fun givenAddFavoriteWhenRemoteReturnsNetworkErrorThenReturnsNetworkErrorAndDoesNotInsert() =
        runTest {
            val favoriteRemote = FakeFavoriteRemoteDataSource(
                favoriteResult = Result.NetworkError(Exception("Network failure")),
                deleteFavoriteResult = Result.Success(Unit)
            )

            val sut = DefaultFavoriteRepository(
                favoriteRemoteDataSource = favoriteRemote,
                favoriteDao = breedDataBase.favoriteDao()
            )

            val result = sut.addFavorite("image-3")
            val dbFavorite = breedDataBase.favoriteDao().findFavoriteByImageId("image-3")

            assertTrue(result is Result.NetworkError)
            assertEquals("Network failure", (result as Result.NetworkError).exception.message)
            assertTrue(dbFavorite == null)
        }

    @Test
    fun givenDeleteFavoriteWhenFavoriteNotFoundReturnsError() = runTest {
        val favoriteRemote = FakeFavoriteRemoteDataSource(
            deleteFavoriteResult = Result.Success(Unit),
            favoriteResult = Result.Error(Exception("Remote error"))
        )

        val sut = DefaultFavoriteRepository(
            favoriteRemoteDataSource = favoriteRemote,
            favoriteDao = breedDataBase.favoriteDao()
        )

        val result = sut.deleteFavorite("non-existent-image")

        assertTrue(result is Result.Error)
        assertEquals("Favorite not found", (result as Result.Error).exception.message)
    }

    @Test
    fun givenDeleteFavoriteWhenRemoteReturnsSuccessDeletesFavoriteAndReturnsSuccess() = runTest {
        breedDataBase.favoriteDao().insertFavorite(FavoriteEntity(id = 99L, imageId = "to-delete"))

        val favoriteRemote = FakeFavoriteRemoteDataSource(
            deleteFavoriteResult = Result.Success(Unit),
            favoriteResult = Result.Success(99L)
        )

        val sut = DefaultFavoriteRepository(
            favoriteRemoteDataSource = favoriteRemote,
            favoriteDao = breedDataBase.favoriteDao()
        )

        val result = sut.deleteFavorite("to-delete")
        val dbFavorite = breedDataBase.favoriteDao().findFavoriteByImageId("to-delete")

        assertTrue(result is Result.Success)
        assertTrue(dbFavorite == null)
    }

    @Test
    fun givenDeleteFavoriteWhenRemoteReturnsErrorThenReturnsErrorAndKeepsFavorite() = runTest {
        breedDataBase.favoriteDao().insertFavorite(FavoriteEntity(id = 77L, imageId = "keep-me"))

        val favoriteRemote = FakeFavoriteRemoteDataSource(
            deleteFavoriteResult = Result.Error(Exception("Delete failed")),
            favoriteResult = Result.Success(77L)
        )

        val sut = DefaultFavoriteRepository(
            favoriteRemoteDataSource = favoriteRemote,
            favoriteDao = breedDataBase.favoriteDao()
        )

        val result = sut.deleteFavorite("keep-me")
        val dbFavorite = breedDataBase.favoriteDao().findFavoriteByImageId("keep-me")

        assertTrue(result is Result.Error)
        assertEquals("Delete failed", (result as Result.Error).exception.message)
        assertNotNull(dbFavorite)
    }

    @Test
    fun giveDeleteFavoriteWhenRemoteReturnsNetworkErrorThenReturnsNetworkErrorAndKeepFavorite() =
        runTest {
            breedDataBase.favoriteDao()
                .insertFavorite(FavoriteEntity(id = 88L, imageId = "keep-network"))

            val favoriteRemote = FakeFavoriteRemoteDataSource(
                deleteFavoriteResult = Result.NetworkError(Exception("Network during delete")),
                favoriteResult = Result.Success(88L)
            )

            val sut = DefaultFavoriteRepository(
                favoriteRemoteDataSource = favoriteRemote,
                favoriteDao = breedDataBase.favoriteDao()
            )

            val result = sut.deleteFavorite("keep-network")
            val dbFavorite = breedDataBase.favoriteDao().findFavoriteByImageId("keep-network")

            assertTrue(result is Result.NetworkError)
            assertEquals("Network during delete", (result as Result.NetworkError).exception.message)
            assertNotNull(dbFavorite)
        }

}