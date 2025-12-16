package com.alexandrenavarro.catlovers.data.repository

import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import com.alexandrenavarro.catlovers.data.network.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DefaultBreedRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val breedRemoteDataSource = mockk<BreedRemoteDataSource>()

    private lateinit var sut: DefaultBreedRepository


    @Before
    fun setup() {
        sut = DefaultBreedRepository(breedRemoteDataSource)
    }

    @Test
    fun `given repository just created when observing breeds then emits empty list`() = runTest {
        sut.getBreeds().test {
            assertEquals(emptyList<BreedPreview>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given refresh is called and result is empty when observing breeds then emits empty list`() = runTest {
        coEvery { breedRemoteDataSource.fetchBreeds() } returns Result.Success(emptyList())

        sut.refreshBreeds()

        sut.getBreeds().test {
            assertEquals(emptyList<BreedPreview>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { breedRemoteDataSource.fetchBreeds() }
    }
}