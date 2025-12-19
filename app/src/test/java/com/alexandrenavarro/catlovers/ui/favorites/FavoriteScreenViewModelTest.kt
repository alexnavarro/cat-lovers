package com.alexandrenavarro.catlovers.ui.favorites

import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.repository.FavoriteRepository
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import com.alexandrenavarro.catlovers.domain.model.FavoriteBreed
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val favoriteRepository: FavoriteRepository = mockk()

    @Test
    fun `uiState initial value is empty list when repository hasn't emitted`() = runTest {
        val repoFlow = MutableSharedFlow<List<FavoriteBreed>>(replay = 0)
        every { favoriteRepository.getFavoriteBreeds() } returns repoFlow

        val sut = FavoriteScreenViewModel(favoriteRepository)

        sut.uiState.test {
            assertEquals(emptyList<FavoriteBreed>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState updates when repository emits new list`() = runTest {
        val repoFlow = MutableStateFlow<List<FavoriteBreed>>(emptyList())
        every { favoriteRepository.getFavoriteBreeds() } returns repoFlow

        val sut = FavoriteScreenViewModel(favoriteRepository)

        sut.uiState.test {
            assertEquals(emptyList<FavoriteBreed>(), awaitItem())

            val newList = listOf(FavoriteBreed(favoriteId = 1L, breedId = "img-1", lifeSpan = 10, imageUrl = "https://example.com/tes.gif"))
            repoFlow.value = newList

            assertEquals(newList, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits multiple updates from repository flow`() = runTest {
        val repoFlow = MutableStateFlow<List<FavoriteBreed>>(emptyList())
        every { favoriteRepository.getFavoriteBreeds() } returns repoFlow

        val sut = FavoriteScreenViewModel(favoriteRepository)

        sut.uiState.test {
            assertEquals(emptyList<FavoriteBreed>(), awaitItem())

            val first = listOf(FavoriteBreed(favoriteId = 1L, breedId = "img-1", lifeSpan = 10, imageUrl = "https://example.com/tes.gif"))
            repoFlow.value = first
            assertEquals(first, awaitItem())

            val second = listOf(
                FavoriteBreed(favoriteId = 2L, breedId = "img-2", lifeSpan = 10, imageUrl = "https://example.com/tes2.gif"),
                FavoriteBreed(favoriteId = 3L, breedId = "img-5", lifeSpan = 15, imageUrl = "https://example.com/tes5.gif")
            )
            repoFlow.value = second
            assertEquals(second, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}