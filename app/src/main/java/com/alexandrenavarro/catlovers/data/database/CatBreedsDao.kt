package com.alexandrenavarro.catlovers.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandrenavarro.catlovers.data.database.model.CatBreedPreviewEntity
import com.alexandrenavarro.catlovers.domain.model.CatBreedPreview

@Dao
interface CatBreedsDao {
    @Query("""
        SELECT 
            cat.id,
            cat.name,
            cat.image_url AS imageUrl,
            cat.image_id AS imageId,
            (f.image_id IS NOT NULL) AS isFavorite
        FROM cat_breeds AS cat
        LEFT JOIN favorites AS f ON cat.image_id = f.image_id
        WHERE (:query IS NULL OR cat.name LIKE '%' || :query || '%' COLLATE NOCASE)
        ORDER BY cat.name
    """,
    )
    fun pagingSource(query: String?): PagingSource<Int, CatBreedPreview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CatBreedPreviewEntity>)

    @Query("DELETE FROM cat_breeds")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM cat_breeds")
    suspend fun count(): Int
}