package com.alexandrenavarro.catlovers.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandrenavarro.catlovers.data.database.model.CatBreedRemoteKey

@Dao
interface CatBreedsRemoteKeyDao {

    @Query("SELECT * FROM cat_breeds_remote_keys WHERE breedId = :id")
    suspend fun remoteKeyById(id: String): CatBreedRemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<CatBreedRemoteKey>)

    @Query("DELETE FROM cat_breeds_remote_keys")
    suspend fun clearRemoteKeys()
}