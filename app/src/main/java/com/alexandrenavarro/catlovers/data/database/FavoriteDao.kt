package com.alexandrenavarro.catlovers.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity
import com.alexandrenavarro.catlovers.domain.model.FavoriteBreed
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :favoriteId")
    suspend fun deleteById(favoriteId: Long)

    @Query("DELETE FROM favorites WHERE image_id = :imageId")
    suspend fun deleteByImageId(imageId: String)

    @Query("SELECT * FROM favorites WHERE image_id = :imageId")
    suspend fun findFavoriteByImageId(imageId: String): FavoriteEntity?

    @Query("""
        SELECT 
            f.id AS favoriteId,
            b.id AS breedId,
            b.image_url AS imageUrl,
            b.average_life_span AS lifeSpan
        FROM favorites AS f
        INNER JOIN breeds AS b ON f.image_id = b.image_id
    """)
    fun getFavoriteBreeds(): Flow<List<FavoriteBreed>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE image_id = :imageId)")
    fun isFavorite(imageId: String): Flow<Boolean>
}