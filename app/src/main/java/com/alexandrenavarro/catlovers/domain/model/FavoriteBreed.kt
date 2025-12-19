package com.alexandrenavarro.catlovers.domain.model

data class FavoriteBreed(
    val favoriteId: Long,
    val breedId: String,
    val imageUrl: String,
    val lifeSpan: Int,
)