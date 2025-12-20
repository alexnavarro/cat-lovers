package com.alexandrenavarro.catlovers.ui.catslist

import androidx.paging.PagingData
import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.repository.CatBreedRepository
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import com.alexandrenavarro.catlovers.domain.model.CatBreedPreview
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatBreedsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val catBreedRepository: CatBreedRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()

    @Test
    fun `breeds flow should emit paging data from repository`() = runTest {
        val mockPagingData = PagingData.from(listOf<CatBreedPreview>())
        val mockFlow = flowOf(mockPagingData)
        every { favoriteRepository.observeFavorites() } returns emptyFlow()

        every { catBreedRepository.getCatBreeds(any()) } returns mockFlow

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

        sut.breeds.test {
            val emission = awaitItem()
            assertNotNull(emission)

            verify(exactly = 1) { catBreedRepository.getCatBreeds(any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFavoriteClicked when adding favorite and remote success emits FavoriteAdded`() = runTest {
        val mockPagingData = PagingData.from(listOf<CatBreedPreview>())
        coEvery { catBreedRepository.getCatBreeds(any()) } returns flowOf(mockPagingData)
        every { favoriteRepository.observeFavorites() } returns emptyFlow()

        coEvery { favoriteRepository.addFavorite("img-1") } returns Result.Success(Unit)

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

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
        val mockPagingData = PagingData.from(listOf<CatBreedPreview>())
        coEvery { catBreedRepository.getCatBreeds(any()) } returns flowOf(mockPagingData)
        every { favoriteRepository.observeFavorites() } returns emptyFlow()

        coEvery { favoriteRepository.deleteFavorite("img-2") } returns Result.Success(Unit)

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

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
        val mockPagingData = PagingData.from(listOf<CatBreedPreview>())
        coEvery { catBreedRepository.getCatBreeds(any()) } returns flowOf(mockPagingData)
        every { favoriteRepository.observeFavorites() } returns emptyFlow()


        coEvery { favoriteRepository.addFavorite("img-3") } returns Result.Error(Exception("Remote failure"))

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

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
        val mockPagingData = PagingData.from(listOf<CatBreedPreview>())
        coEvery { catBreedRepository.getCatBreeds(any()) } returns flowOf(mockPagingData)
        every { favoriteRepository.observeFavorites() } returns emptyFlow()

        coEvery { favoriteRepository.deleteFavorite("img-4") } returns Result.NetworkError(Exception("No net"))

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

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
        val mockPagingData = PagingData.from(listOf<CatBreedPreview>())
        val mockFlow = flowOf(mockPagingData)
        every { favoriteRepository.observeFavorites() } returns emptyFlow()

        every { catBreedRepository.getCatBreeds(null) } returns mockFlow

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

        sut.breeds.test {
            val emission = awaitItem()
            assertNotNull(emission)

            verify(exactly = 1) { catBreedRepository.getCatBreeds(null) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setQuery updates query and calls repository with new value after debounce`() = runTest {
        val flowNull = flowOf(PagingData.from(listOf<CatBreedPreview>()))
        val flowSiamese = flowOf(PagingData.from(listOf<CatBreedPreview>()))
        every { favoriteRepository.observeFavorites() } returns emptyFlow()

        every { catBreedRepository.getCatBreeds(null) } returns flowNull
        every { catBreedRepository.getCatBreeds("siamese") } returns flowSiamese

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

        sut.breeds.test {
            awaitItem()

            sut.setQuery("siamese")
            advanceTimeBy(300)

            awaitItem()
            verify { catBreedRepository.getCatBreeds("siamese") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setQuery blank becomes null and calls repository with null`() = runTest {
        every { catBreedRepository.getCatBreeds(null) } returns flowOf(PagingData.from(listOf()))
        every { catBreedRepository.getCatBreeds("tmp") } returns flowOf(PagingData.from(listOf()))
        every { favoriteRepository.observeFavorites() } returns emptyFlow()

        val sut = CatBreedsScreenViewModel(catBreedRepository, favoriteRepository)

        sut.breeds.test {
            awaitItem()

            sut.setQuery("tmp")
            advanceTimeBy(300)
            awaitItem()

            sut.setQuery("")
            advanceTimeBy(300)
            awaitItem()

            verify(atLeast = 1) { catBreedRepository.getCatBreeds(null) }

            cancelAndIgnoreRemainingEvents()
        }
    }
}