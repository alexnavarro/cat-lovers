package com.alexandrenavarro.catlovers.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alexandrenavarro.catlovers.data.database.CatBreedsDatabase
import com.alexandrenavarro.catlovers.data.database.model.CatBreedPreviewEntity
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity
import com.alexandrenavarro.catlovers.data.network.Result
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


class FavoriteRepositoryImplTest {
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
    fun givenAddFavoriteWhenRemoteReturnSuccessInsertsFavoritesAndReturnsSuccess() = runTest {
        val favoriteResult = Result.Success(42L)
        val favoriteRemote =
            FakeFavoriteRemoteDataSource(favoriteResult = favoriteResult, Result.Success(Unit))

        val sut = FavoriteRepositoryImpl(
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

        val sut = FavoriteRepositoryImpl(
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

            val sut = FavoriteRepositoryImpl(
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

        val sut = FavoriteRepositoryImpl(
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

        val sut = FavoriteRepositoryImpl(
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

        val sut = FavoriteRepositoryImpl(
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

            val sut = FavoriteRepositoryImpl(
                favoriteRemoteDataSource = favoriteRemote,
                favoriteDao = breedDataBase.favoriteDao()
            )

            val result = sut.deleteFavorite("keep-network")
            val dbFavorite = breedDataBase.favoriteDao().findFavoriteByImageId("keep-network")

            assertTrue(result is Result.NetworkError)
            assertEquals("Network during delete", (result as Result.NetworkError).exception.message)
            assertNotNull(dbFavorite)
        }

    @Test
    fun givenGetFavoriteBreedsWhenNoFavoritesThenReturnsEmptyList() = runTest {
        val favoriteRemote =
            FakeFavoriteRemoteDataSource(favoriteResult = Result.Success(1L), deleteFavoriteResult = Result.Success(Unit))

        val sut = FavoriteRepositoryImpl(
            favoriteRemoteDataSource = favoriteRemote,
            favoriteDao = breedDataBase.favoriteDao()
        )

        val favorites = sut.getFavoriteBreeds().first()
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun givenGetFavoriteBreedsReturnsInsertedFavoriteMappedToDomainModel() = runTest {
        breedDataBase.catBreedsDao().insertAll(listOf(
            CatBreedPreviewEntity(id = "444", name = "Abyssinian", imageUrl = "https://cdn2.thecatapi.com/images/O3btzLlsO.png", imageId = "img-5", averageLifeSpan = 18)
        ))
        breedDataBase.favoriteDao().insertFavorite(FavoriteEntity(id = 5L, imageId = "img-5"))

        val favoriteRemote =
            FakeFavoriteRemoteDataSource(favoriteResult = Result.Success(1L), deleteFavoriteResult = Result.Success(Unit))

        val sut = FavoriteRepositoryImpl(
            favoriteRemoteDataSource = favoriteRemote,
            favoriteDao = breedDataBase.favoriteDao()
        )

        val favorites = sut.getFavoriteBreeds().first()

        assertEquals(1, favorites.size)
        val fav = favorites[0]
        assertEquals(5L, fav.favoriteId)
        assertEquals("444", fav.breedId)
        assertEquals(18, fav.lifeSpan)
        assertEquals("https://cdn2.thecatapi.com/images/O3btzLlsO.png", fav.imageUrl)
    }

}