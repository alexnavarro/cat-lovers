package com.alexandrenavarro.catlovers.data.repository

import app.cash.turbine.test
import com.alexandrenavarro.catlovers.data.model.BreedPreview
import com.alexandrenavarro.catlovers.data.network.Result
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedImage
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DefaultBreedRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `given repository just created when observing breeds then emits empty list`() = runTest {
        val sut = DefaultBreedRepository(FakeBreedRemoteDataSource(Result.Success(emptyList())))

        sut.getBreeds().test {
            assertEquals(emptyList<BreedPreview>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given refresh is called and result is empty when observing breeds then emits empty list`() =
        runTest {
            val dataSource = FakeBreedRemoteDataSource(Result.Success(emptyList()))

            val sut = DefaultBreedRepository(dataSource)

            sut.getBreeds().test {
                assertEquals(emptyList<BreedPreview>(), awaitItem())
                sut.refreshBreeds()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given remote returns breeds when refreshBreeds is called then flow emits mapped breeds`() =
        runTest {
            val dataSource = FakeBreedRemoteDataSource(
                Result.Success(
                    listOf(
                        NetworkBreedPreview(
                            "1",
                            "name",
                            image = NetworkBreedImage(
                                id = "id",
                                imageUrl = "url"
                            )
                        )
                    )
                )
            )

            val sut = DefaultBreedRepository(dataSource)

            sut.getBreeds().test {
                assertEquals(emptyList<BreedPreview>(), awaitItem())
                sut.refreshBreeds()
                val emitted = awaitItem()
                assertEquals(1, emitted.size)
                assertEquals("1", emitted.first().id)
                assertEquals("name", emitted.first().name)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given existing data and remote failure when refreshBreeds is called Then cached data is preserved`() =
        runTest {
            val dataSource = FakeBreedRemoteDataSource(Result.Success(listOf(
                NetworkBreedPreview(
                    "1",
                    "name",
                    image = NetworkBreedImage(
                        id = "id",
                        imageUrl = "url"
                    )
                )
            )))

            val sut = DefaultBreedRepository(dataSource)
            sut.refreshBreeds()
            dataSource.shouldReturnError = true

            sut.getBreeds().test {
                val cached = awaitItem()
                assertEquals(1, cached.size)

                val failingResult = sut.refreshBreeds()
                assertTrue(failingResult is Result.Error)

                expectNoEvents()
            }
        }
}