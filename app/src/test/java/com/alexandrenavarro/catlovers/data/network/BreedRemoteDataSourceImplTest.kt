package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedDetail
import com.alexandrenavarro.catlovers.data.network.model.NetworkBreedPreview
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException


class BreedRemoteDataSourceImplTest {

    private val breedApi = mockk<BreedApi>()
    private val response: Response<List<NetworkBreedPreview>> = mockk()
    private val breedDetailResponse: Response<NetworkBreedDetail> = mockk()

    private lateinit var sut: BreedRemoteDataSourceImpl

    @Before
    fun setup() {
        sut = BreedRemoteDataSourceImpl(breedApi)
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
    fun `given fetchBreeds call is called when there is an error on response then return an error`() =
        runTest {
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
            val breeds = listOf(NetworkBreedPreview("1", "name", mockk(), lifeSpan = "10 - 20"))

            coEvery { response.isSuccessful } returns true
            coEvery { response.body() } returns breeds
            coEvery { breedApi.fetchBreeds(limit = 10, page = 0) } returns response

            val result = sut.fetchBreeds() as Result.Success

            assertSame(breeds, result.data)
        }

    @Test
    fun `given fetchBreed called when there is a network error then return a network error`() =
        runTest {
            coEvery { breedApi.fetchBreed("1") } throws IOException()

            val result = sut.fetchBreed("1")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given fetchBreed called when there is an unknown error then return a network error`() =
        runTest {
            coEvery { breedApi.fetchBreed("1") } throws Exception()

            val result = sut.fetchBreed("1")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given fetchBreed called when response is not successful then return an error`() =
        runTest {
            coEvery { breedDetailResponse.isSuccessful } returns false
            coEvery { breedDetailResponse.message() } returns "HTTP failure"
            coEvery { breedApi.fetchBreed("1") } returns breedDetailResponse

            val result = sut.fetchBreed("1")
            assert(result is Result.Error)
        }

    @Test
    fun `given fetchBreed called when response is successful and body present then return success`() =
        runTest {
            val detail = mockk<NetworkBreedDetail>()
            coEvery { breedDetailResponse.isSuccessful } returns true
            coEvery { breedDetailResponse.body() } returns detail
            coEvery { breedApi.fetchBreed("1") } returns breedDetailResponse

            val result = sut.fetchBreed("1") as Result.Success

            assertSame(detail, result.data)
        }

    @Test
    fun `given fetchBreed called when response is successful but body is null then return network error`() =
        runTest {
            coEvery { breedDetailResponse.isSuccessful } returns true
            coEvery { breedDetailResponse.body() } returns null
            coEvery { breedApi.fetchBreed("1") } returns breedDetailResponse

            val result = sut.fetchBreed("1")
            assertTrue(result is Result.NetworkError)
        }
}