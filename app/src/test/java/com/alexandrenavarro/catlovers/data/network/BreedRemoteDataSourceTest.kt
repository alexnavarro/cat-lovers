package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException


class BreedRemoteDataSourceTest {

    private val breedApi = mockk<BreedApi>()
    private val response: Response<List<NetworkBreedPreview>> = mockk()

    private lateinit var sut: BreedRemoteDataSource

    @Before
    fun setup() {
        sut = BreedRemoteDataSource(breedApi)
    }

    @Test
    fun `given fetchBreeds is called when there is a network error then return a network error`() =
        runTest {
            coEvery { breedApi.fetchBreeds(limit = 10, page = 0) } throws IOException()

            val result = sut.fetchBreeds()
            assert(result is Result.NetworkError)
        }
}