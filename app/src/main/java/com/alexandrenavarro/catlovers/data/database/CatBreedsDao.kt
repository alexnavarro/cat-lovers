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
        id,
        name,
        image_url AS imageUrl,
        image_id AS imageId
    FROM cat_breeds
    WHERE (:query IS NULL OR name LIKE '%' || :query || '%' COLLATE NOCASE)
    ORDER BY name
""")
    fun pagingSource(query: String?): PagingSource<Int, CatBreedPreview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CatBreedPreviewEntity>)

    @Query("DELETE FROM cat_breeds")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM cat_breeds")
    suspend fun count(): Int
}