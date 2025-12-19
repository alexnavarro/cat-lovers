package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkAddFavoriteResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class FavoriteRemoteDataSourceImplTest {

    private val favoriteApi = mockk<FavoriteApi>()

    private val responseFavorite: Response<NetworkAddFavoriteResponse> = mockk()
    private val responseDelete: Response<Unit> = mockk()

    private lateinit var sut: FavoriteRemoteDataSourceImpl

    @Before
    fun setUp(){
        sut = FavoriteRemoteDataSourceImpl(favoriteApi)
    }

    @Test
    fun `given favorite is called when there is a network error then return a network error`() =
        runTest {
            coEvery { favoriteApi.addFavorite(any())} throws IOException()

            val result = sut.favorite("xxxx")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given favorite call is called when there is a unknown error then return a network error`() =
        runTest {
            coEvery { favoriteApi.addFavorite(any()) } throws Exception()

            val result = sut.favorite("xxxx")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given favorite call is called when there is an error on response then return an error`() = runTest {
        coEvery { responseFavorite.isSuccessful } returns false
        coEvery { responseFavorite.message() } returns "Error error"
        coEvery { favoriteApi.addFavorite(any()) } returns responseFavorite

        val result = sut.favorite("xxxx")
        assert(result is Result.Error)
    }

    @Test
    fun `given favorite call is called when there is an empty response then return a success`() =
        runTest {
            coEvery { responseFavorite.isSuccessful } returns true
            coEvery { responseFavorite.body() } returns NetworkAddFavoriteResponse(0)
            coEvery { favoriteApi.addFavorite(any()) } returns responseFavorite

            val result = sut.favorite("xxxx") as Result.Success

            assertTrue(result.data == 0L)
        }

    @Test
    fun `given deleteFavorite is called when there is a network error then return a network error`() =
        runTest {
            coEvery { favoriteApi.deleteFavorite(any()) } throws IOException()

            val result = sut.deleteFavorite(123L)
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given deleteFavorite is called when there is an unknown error then return a network error`() =
        runTest {
            coEvery { favoriteApi.deleteFavorite(any()) } throws Exception()

            val result = sut.deleteFavorite(123L)
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given deleteFavorite is called when there is an error on response then return an error`() =
        runTest {
            coEvery { responseDelete.isSuccessful } returns false
            coEvery { responseDelete.message() } returns "Delete error"
            coEvery { favoriteApi.deleteFavorite(any()) } returns responseDelete

            val result = sut.deleteFavorite(123L)
            assert(result is Result.Error)
        }

    @Test
    fun `given deleteFavorite is called when response is successful then return success`() =
        runTest {
            coEvery { responseDelete.body() } returns Unit
            coEvery { responseDelete.isSuccessful } returns true
            coEvery { favoriteApi.deleteFavorite(any()) } returns responseDelete

            val result = sut.deleteFavorite(123L) as Result.Success

            assertTrue(result.data == Unit)
        }

}