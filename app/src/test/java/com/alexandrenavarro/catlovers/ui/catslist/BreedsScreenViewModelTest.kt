package com.alexandrenavarro.catlovers.ui.catslist

import androidx.paging.PagingData
import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class BreedsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val breedRepository: BreedRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()

    @Test
    fun `breeds flow should emit paging data from repository`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        val mockFlow = flowOf(mockPagingData)

        every { breedRepository.getBreeds() } returns mockFlow

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.breeds.test {
            val emission = awaitItem()
            assertNotNull(emission)

            verify(exactly = 1) { breedRepository.getBreeds() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClicked when adding favorite and remote success emits FavoriteAdded`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        coEvery { breedRepository.getBreeds() } returns flowOf(mockPagingData)

        coEvery { favoriteRepository.addFavorite("img-1") } returns Result.Success(Unit)

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.events.test {
            sut.onFavoriteClicked("img-1", isFavorite = false)

            val event = awaitItem()
            assertTrue(event is FavoriteUiEvent.FavoriteAdded)
            coVerify(exactly = 1) { favoriteRepository.addFavorite("img-1") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClicked when removing favorite and remote success emits FavoriteRemoved`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        coEvery { breedRepository.getBreeds() } returns flowOf(mockPagingData)

        coEvery { favoriteRepository.deleteFavorite("img-2") } returns Result.Success(Unit)

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.events.test {
            sut.onFavoriteClicked("img-2", isFavorite = true)

            val event = awaitItem()
            assertTrue(event is FavoriteUiEvent.FavoriteRemoved)
            coVerify(exactly = 1) { favoriteRepository.deleteFavorite("img-2") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClicked when addFavorite returns Error emits Error message`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        coEvery { breedRepository.getBreeds() } returns flowOf(mockPagingData)

        coEvery { favoriteRepository.addFavorite("img-3") } returns Result.Error(Exception("Remote failure"))

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.events.test {
            sut.onFavoriteClicked("img-3", isFavorite = false)

            val event = awaitItem()
            assertTrue(event is FavoriteUiEvent.Error)
            assertEquals("Failed to update favorite", (event as FavoriteUiEvent.Error).message)
            coVerify(exactly = 1) { favoriteRepository.addFavorite("img-3") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClicked when deleteFavorite returns NetworkError emits network message`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        coEvery { breedRepository.getBreeds() } returns flowOf(mockPagingData)

        coEvery { favoriteRepository.deleteFavorite("img-4") } returns Result.NetworkError(Exception("No net"))

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.events.test {
            sut.onFavoriteClicked("img-4", isFavorite = true)

            val event = awaitItem()
            assertTrue(event is FavoriteUiEvent.Error)
            assertEquals("No internet connection", (event as FavoriteUiEvent.Error).message)
            coVerify(exactly = 1) { favoriteRepository.deleteFavorite("img-4") }

            cancelAndIgnoreRemainingEvents()
        }
    }
}