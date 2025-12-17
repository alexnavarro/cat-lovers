package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import com.alexandrenavarro.catlovers.data.network.model.NetworkFavoriteResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException


class DefaultBreedRemoteDataSourceTest {

    private val breedApi = mockk<BreedApi>()
    private val response: Response<List<NetworkBreedPreview>> = mockk()
    private val responseFavorite: Response<NetworkFavoriteResponse> = mockk()

    private lateinit var sut: DefaultBreedRemoteDataSource

    @Before
    fun setup() {
        sut = DefaultBreedRemoteDataSource(breedApi)
    }

    @Test
    fun `given fetchBreeds is called when there is a network error then return a network error`() =
        runTest {
            coEvery { breedApi.fetchBreeds(limit = 10, page = 0) } throws IOException()

            val result = sut.fetchBreeds()
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given fetchBreeds call is called when there is a unknown error then return a network error`() =
        runTest {
            coEvery { breedApi.fetchBreeds(limit = 10, page = 0) } throws Exception()

            val result = sut.fetchBreeds()
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given fetchBreeds call is called when there is an error on response then return an error`() = runTest {
        coEvery { response.isSuccessful } returns false
        coEvery { response.message() } returns "Error error"
        coEvery { breedApi.fetchBreeds(limit = 10, page = 0) } returns response

        val result = sut.fetchBreeds()
        assert(result is Result.Error)
    }

    @Test
    fun `given fetchBreeds call is called when there is an empty response then return a success`() =
        runTest {
            coEvery { response.isSuccessful } returns true
            coEvery { response.body() } returns emptyList()
            coEvery { breedApi.fetchBreeds(limit = 10, page = 0) } returns response

            val result = sut.fetchBreeds() as Result.Success

            assertTrue(result.data.isEmpty())
        }

    @Test
    fun `given fetch call is called twice when there is a valid response then should call the api only once`() =
        runTest {
            val breeds = listOf(NetworkBreedPreview("1", "name", mockk()))

            coEvery { response.isSuccessful } returns true
            coEvery { response.body() } returns breeds
            coEvery { breedApi.fetchBreeds(limit = 10, page = 0) } returns response

            val result = sut.fetchBreeds() as Result.Success

            assertSame(breeds, result.data)
        }

    @Test
    fun `given favorite is called when there is a network error then return a network error`() =
        runTest {
            coEvery { breedApi.favorite(any())} throws IOException()

            val result = sut.favorite("xxxx")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given favorite call is called when there is a unknown error then return a network error`() =
        runTest {
            coEvery { breedApi.favorite(any()) } throws Exception()

            val result = sut.favorite("xxxx")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given favorite call is called when there is an error on response then return an error`() = runTest {
        coEvery { responseFavorite.isSuccessful } returns false
        coEvery { responseFavorite.message() } returns "Error error"
        coEvery { breedApi.favorite(any()) } returns responseFavorite

        val result = sut.favorite("xxxx")
        assert(result is Result.Error)
    }

    @Test
    fun `given favorite call is called when there is an empty response then return a success`() =
        runTest {
            coEvery { responseFavorite.isSuccessful } returns true
            coEvery { responseFavorite.body() } returns NetworkFavoriteResponse(0)
            coEvery { breedApi.favorite(any()) } returns responseFavorite

            val result = sut.favorite("xxxx") as Result.Success

            assertTrue(result.data == 0L)
        }
}