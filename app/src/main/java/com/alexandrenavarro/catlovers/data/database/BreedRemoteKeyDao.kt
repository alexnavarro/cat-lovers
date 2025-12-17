package com.alexandrenavarro.catlovers.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandrenavarro.catlovers.data.database.model.BreedRemoteKey

@Dao
interface BreedRemoteKeyDao {

    @Query("SELECT * FROM breed_remote_keys WHERE breedId = :id")
    suspend fun remoteKeyById(id: String): BreedRemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<BreedRemoteKey>)

    @Query("DELETE FROM breed_remote_keys")
    suspend fun clearRemoteKeys()
}