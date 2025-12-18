package com.alexandrenavarro.catlovers.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandrenavarro.catlovers.data.database.model.FavoriteEntity

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

}