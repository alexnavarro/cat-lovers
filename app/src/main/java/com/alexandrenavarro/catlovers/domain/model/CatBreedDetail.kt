package com.alexandrenavarro.catlovers.domain.model

data class CatBreedDetail (
    val id: String,
    val name: String,
    val imageUrl: String?,
    val imageId: String?,
    val origin: String,
    val description: String,
    val temperament: String,
)