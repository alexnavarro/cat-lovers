package com.alexandrenavarro.catlovers.ui.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import com.alexandrenavarro.catlovers.domain.model.BreedDetail
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

class BreedDetailScreenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val breedRepository: BreedRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk(relaxed = true)

    @Test
    fun `uiState when remote returns success emits Success with breed and favorite flag`() = runTest {
        val breedId = "1"
        val imageId = "1xsed"
        val breedDetail = mockk<BreedDetail>()
        coEvery { breedRepository.getBreedDetail(breedId) } returns Result.Success(breedDetail)
        every { favoriteRepository.isFavorite(imageId) } returns flowOf(true)

        val sut = BreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            breedRepository = breedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedId, "imageId" to imageId))
        )

        sut.uiState.test {
            val first = awaitItem()
            assertNotNull(first)
            assertTrue(first is BreedDetailUiState.Loading)

            val second = awaitItem()
            assertTrue(second is BreedDetailUiState.Success)
            val success = second as BreedDetailUiState.Success
            assertSame(breedDetail, success.breedDetail)
            assertTrue(success.isFavorite)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState when remote returns error emits Error`() = runTest {
        val breedId = "1"
        val imageId = "1xsed"
        coEvery { breedRepository.getBreedDetail(breedId) } returns Result.Error(Exception("Remote fail"))
        every { favoriteRepository.isFavorite(imageId) } returns flowOf(false)

        val sut = BreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            breedRepository = breedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedId, "imageId" to imageId))
        )

        sut.uiState.test {
            val first = awaitItem()
            assertTrue(first is BreedDetailUiState.Loading)

            val second = awaitItem()
            assertTrue(second is BreedDetailUiState.Error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState when remote returns network error remains Loading`() = runTest {
        val breedId = "1"
        val imageId = "1xsed"
        coEvery { breedRepository.getBreedDetail(breedId) } returns Result.NetworkError(Exception("No net"))
        every { favoriteRepository.isFavorite(breedId) } returns flowOf(false)

        val sut = BreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            breedRepository = breedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedId, "imageId" to imageId))
        )

        sut.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedDetailUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onFavoriteToggle when not favorite calls addFavorite`() = runTest {
        val breedDetail = BreedDetail(
            id = "1",
            name = "Abyssinian",
            description = "description",
            temperament = "temperament",
            origin = "origin",
            imageUrl = "url",
            imageId = "id"
        )

        coEvery { breedRepository.getBreedDetail(any()) } returns Result.Success(breedDetail)
        every { favoriteRepository.isFavorite(any()) } returns flowOf(false)
        coEvery { favoriteRepository.addFavorite(any()) } returns Result.Success(Unit)

        val sut = BreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            breedRepository = breedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedDetail.id, "imageId" to breedDetail.imageId))
        )

        sut.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedDetailUiState.Loading)
            val second = awaitItem() as BreedDetailUiState.Success
            assertFalse(second.isFavorite)
            sut.onFavoriteToggle(breedDetail.imageId!!)
            advanceTimeBy(300)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { favoriteRepository.addFavorite(imageId = any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onFavoriteToggle when already favorite calls deleteFavorite`() = runTest {
        val breedDetail = BreedDetail(
            id = "1",
            name = "Abyssinian",
            description = "description",
            temperament = "temperament",
            origin = "origin",
            imageUrl = "url",
            imageId = "id"
        )
        coEvery { breedRepository.getBreedDetail(any()) } returns Result.Success(breedDetail)
        every { favoriteRepository.isFavorite(any()) } returns flowOf(true)
        coEvery { favoriteRepository.deleteFavorite(any()) } returns Result.Success(Unit)

        val sut = BreedDetailScreenViewModel(
            favoriteRepository = favoriteRepository,
            breedRepository = breedRepository,
            savedStateHandle = SavedStateHandle(mapOf("breedId" to breedDetail.id, "imageId" to breedDetail.imageId))
        )

        sut.uiState.test {
            val state = awaitItem()
            assertTrue(state is BreedDetailUiState.Loading)
            val second = awaitItem() as BreedDetailUiState.Success
            assertTrue(second.isFavorite)
            sut.onFavoriteToggle(breedDetail.imageId!!)
            advanceTimeBy(300)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { favoriteRepository.deleteFavorite(breedDetail.imageId!!) }
    }
}