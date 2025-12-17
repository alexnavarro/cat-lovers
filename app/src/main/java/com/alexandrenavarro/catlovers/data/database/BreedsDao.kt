package com.alexandrenavarro.catlovers.data.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BreedsDao {

    @Query("DELETE FROM breeds")
    suspend fun clearAll()
}