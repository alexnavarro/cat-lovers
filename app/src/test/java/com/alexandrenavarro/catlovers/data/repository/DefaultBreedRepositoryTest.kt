package com.alexandrenavarro.catlovers.data.repository

import com.alexandrenavarro.catlovers.data.network.BreedRemoteDataSource
import com.alexandrenavarro.catlovers.data.util.MainDispatcherRule
import io.mockk.mockk
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

    }



}