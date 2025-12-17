package com.alexandrenavarro.catlovers.ui.catslist

import androidx.paging.PagingData
import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.repository.BreedRepository
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import com.alexandrenavarro.catlovers.domain.model.BreedPreview
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class BreedsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val breedRepository: BreedRepository = mockk()

    @Test
    fun `breeds flow should emit paging data from repository`() = runTest {
        val mockPagingData = PagingData.from(listOf<BreedPreview>())
        val mockFlow = flowOf(mockPagingData)

        every { breedRepository.getBreeds() } returns mockFlow

        val sut = BreedsScreenViewModel(breedRepository)

        sut.breeds.test {
            val emission = awaitItem()
            assertNotNull(emission)

            verify(exactly = 1) { breedRepository.getBreeds() }

            cancelAndIgnoreRemainingEvents()
        }
    }

}