package com.alexandrenavarro.catlovers.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandrenavarro.catlovers.data.database.model.BreedPreviewEntity
import com.alexandrenavarro.catlovers.domain.model.BreedPreview

@Dao
interface BreedsDao {
    @Query("""
        SELECT 
            breed.id,
            breed.name,
            breed.image_url AS imageUrl,
            breed.image_id AS imageId,
            (f.image_id IS NOT NULL) AS isFavorite
        FROM breeds AS breed
        LEFT JOIN favorites AS f ON breed.image_id = f.image_id
        WHERE (:query IS NULL OR breed.name LIKE '%' || :query || '%' COLLATE NOCASE)
        ORDER BY breed.name
    """,
    )
    fun pagingSource(query: String?): PagingSource<Int, BreedPreview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BreedPreviewEntity>)

    @Query("DELETE FROM breeds")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM breeds")
    suspend fun count(): Int
}