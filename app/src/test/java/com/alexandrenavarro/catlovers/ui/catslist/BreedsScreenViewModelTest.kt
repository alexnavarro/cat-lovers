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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BreedsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val breedRepository: BreedRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()

    @Test
    fun `breeds flow should emit paging data from repository`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        val mockFlow = flowOf(mockPagingData)

        every { breedRepository.getBreeds(any()) } returns mockFlow

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.breeds.test {
            val emission = awaitItem()
            assertNotNull(emission)

            verify(exactly = 1) { breedRepository.getBreeds(any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClicked when adding favorite and remote success emits FavoriteAdded`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        coEvery { breedRepository.getBreeds(any()) } returns flowOf(mockPagingData)

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
        coEvery { breedRepository.getBreeds(any()) } returns flowOf(mockPagingData)

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
        coEvery { breedRepository.getBreeds(any()) } returns flowOf(mockPagingData)

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
        coEvery { breedRepository.getBreeds(any()) } returns flowOf(mockPagingData)

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

    @Test
    fun `breeds flow should call repository with initial null query`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        val mockFlow = flowOf(mockPagingData)

        every { breedRepository.getBreeds(null) } returns mockFlow

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.breeds.test {
            val emission = awaitItem()
            assertNotNull(emission)

            verify(exactly = 1) { breedRepository.getBreeds(null) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setQuery updates query and calls repository with new value after debounce`() = runTest {
        val flowNull = flowOf(PagingData.from(listOf<BreedPreview>()))
        val flowSiamese = flowOf(PagingData.from(listOf<BreedPreview>()))

        every { breedRepository.getBreeds(null) } returns flowNull
        every { breedRepository.getBreeds("siamese") } returns flowSiamese

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.breeds.test {
            awaitItem()

            sut.setQuery("siamese")
            advanceTimeBy(300)

            awaitItem()
            verify { breedRepository.getBreeds("siamese") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setQuery blank becomes null and calls repository with null`() = runTest {
        every { breedRepository.getBreeds(null) } returns flowOf(PagingData.from(listOf()))
        every { breedRepository.getBreeds("tmp") } returns flowOf(PagingData.from(listOf()))

        val sut = BreedsScreenViewModel(breedRepository, favoriteRepository)

        sut.breeds.test {
            awaitItem()

            sut.setQuery("tmp")
            advanceTimeBy(300)
            awaitItem()

            sut.setQuery("")
            advanceTimeBy(300)
            awaitItem()

            verify(atLeast = 1) { breedRepository.getBreeds(null) }

            cancelAndIgnoreRemainingEvents()
        }
    }
}