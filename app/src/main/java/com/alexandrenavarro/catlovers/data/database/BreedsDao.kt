package com.alexandrenavarro.catlovers.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandrenavarro.catlovers.data.database.model.BreedPreviewEntity

@Dao
interface BreedsDao {

    @Query("SELECT * FROM breeds ORDER BY name")
    fun pagingSource(): PagingSource<Int, BreedPreviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BreedPreviewEntity>)

    @Query("DELETE FROM breeds")
    suspend fun clearAll()
}