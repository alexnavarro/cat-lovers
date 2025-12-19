package com.alexandrenavarro.catlovers.ui.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.CatBreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import com.alexandrenavarro.catlovers.domain.model.CatBreedDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CatBreedDetailScreenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val catBreedRepository: CatBreedRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk(relaxed = true)

    @Test
    fun `uiState when remote returns success emits Success with breed and favorite flag`() = runTest {
        val breedId = "1"
        val imageId = "1xsed"
        val catBreedDetail = mockk<CatBreedDetail>()
        coEvery { catBreedRepository.getCatBreedDetail(breedId) } returns Result.Success(catBreedDetail)
        every { favoriteRepository.isFavorite(imageId) } returns flowOf(true)

        val sut = CatBreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            catBreedRepository = catBreedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedId, "imageId" to imageId))
        )

        sut.uiState.test {
            val first = awaitItem()
            assertNotNull(first)
            assertTrue(first is CatBreedDetailUiState.Loading)

            val second = awaitItem()
            assertTrue(second is CatBreedDetailUiState.Success)
            val success = second as CatBreedDetailUiState.Success
            assertSame(catBreedDetail, success.catBreedDetail)
            assertTrue(success.isFavorite)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState when remote returns error emits Error`() = runTest {
        val breedId = "1"
        val imageId = "1xsed"
        coEvery { catBreedRepository.getCatBreedDetail(breedId) } returns Result.Error(Exception("Remote fail"))
        every { favoriteRepository.isFavorite(imageId) } returns flowOf(false)

        val sut = CatBreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            catBreedRepository = catBreedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedId, "imageId" to imageId))
        )

        sut.uiState.test {
            val first = awaitItem()
            assertTrue(first is CatBreedDetailUiState.Loading)

            val second = awaitItem()
            assertTrue(second is CatBreedDetailUiState.Error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState when remote returns network error remains Loading`() = runTest {
        val breedId = "1"
        val imageId = "1xsed"
        coEvery { catBreedRepository.getCatBreedDetail(breedId) } returns Result.NetworkError(Exception("No net"))
        every { favoriteRepository.isFavorite(breedId) } returns flowOf(false)

        val sut = CatBreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            catBreedRepository = catBreedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedId, "imageId" to imageId))
        )

        sut.uiState.test {
            val state = awaitItem()
            assertTrue(state is CatBreedDetailUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onFavoriteToggle when not favorite calls addFavorite`() = runTest {
        val catBreedDetail = CatBreedDetail(
            id = "1",
            name = "Abyssinian",
            description = "description",
            temperament = "temperament",
            origin = "origin",
            imageUrl = "url",
            imageId = "id"
        )

        coEvery { catBreedRepository.getCatBreedDetail(any()) } returns Result.Success(catBreedDetail)
        every { favoriteRepository.isFavorite(any()) } returns flowOf(false)
        coEvery { favoriteRepository.addFavorite(any()) } returns Result.Success(Unit)

        val sut = CatBreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            catBreedRepository = catBreedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to catBreedDetail.id, "imageId" to catBreedDetail.imageId))
        )

        sut.uiState.test {
            val state = awaitItem()
            assertTrue(state is CatBreedDetailUiState.Loading)
            val second = awaitItem() as CatBreedDetailUiState.Success
            assertFalse(second.isFavorite)
            sut.onFavoriteToggle(catBreedDetail.imageId!!)
            advanceTimeBy(300)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { favoriteRepository.addFavorite(imageId = any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onFavoriteToggle when already favorite calls deleteFavorite`() = runTest {
        val catBreedDetail = CatBreedDetail(
            id = "1",
            name = "Abyssinian",
            description = "description",
            temperament = "temperament",
            origin = "origin",
            imageUrl = "url",
            imageId = "id"
        )
        coEvery { catBreedRepository.getCatBreedDetail(any()) } returns Result.Success(catBreedDetail)
        every { favoriteRepository.isFavorite(any()) } returns flowOf(true)
        coEvery { favoriteRepository.deleteFavorite(any()) } returns Result.Success(Unit)

        val sut = CatBreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            catBreedRepository = catBreedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to catBreedDetail.id, "imageId" to catBreedDetail.imageId))
        )

        sut.uiState.test {
            val state = awaitItem()
            assertTrue(state is CatBreedDetailUiState.Loading)
            val second = awaitItem() as CatBreedDetailUiState.Success
            assertTrue(second.isFavorite)
            sut.onFavoriteToggle(catBreedDetail.imageId!!)
            advanceTimeBy(300)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { favoriteRepository.deleteFavorite(catBreedDetail.imageId!!) }
    }
}