package com.alexandrenavarro.catlovers.data.network

import com.alexandrenavarro.catlovers.data.network.model.NetworkFavoriteResponse
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class DefaultFavoriteRemoteDataSourceTest {

    private val favoriteApi = mockk<FavoriteApi>()

    private val responseFavorite: Response<NetworkFavoriteResponse> = mockk()

    private lateinit var sut: DefaultFavoriteRemoteDataSource

    @Before
    fun setUp(){
        sut = DefaultFavoriteRemoteDataSource(favoriteApi)
    }

    @Test
    fun `given favorite is called when there is a network error then return a network error`() =
        runTest {
            coEvery { favoriteApi.favorite(any())} throws IOException()

            val result = sut.favorite("xxxx")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given favorite call is called when there is a unknown error then return a network error`() =
        runTest {
            coEvery { favoriteApi.favorite(any()) } throws Exception()

            val result = sut.favorite("xxxx")
            assert(result is Result.NetworkError)
        }

    @Test
    fun `given favorite call is called when there is an error on response then return an error`() = runTest {
        coEvery { responseFavorite.isSuccessful } returns false
        coEvery { responseFavorite.message() } returns "Error error"
        coEvery { favoriteApi.favorite(any()) } returns responseFavorite

        val result = sut.favorite("xxxx")
        assert(result is Result.Error)
    }

    @Test
    fun `given favorite call is called when there is an empty response then return a success`() =
        runTest {
            coEvery { responseFavorite.isSuccessful } returns true
            coEvery { responseFavorite.body() } returns NetworkFavoriteResponse(0)
            coEvery { favoriteApi.favorite(any()) } returns responseFavorite

            val result = sut.favorite("xxxx") as Result.Success

            assertTrue(result.data == 0L)
        }

}