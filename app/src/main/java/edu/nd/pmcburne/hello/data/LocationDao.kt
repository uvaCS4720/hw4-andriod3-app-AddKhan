package edu.nd.pmcburne.hello.data

import androidx.room.*

@Dao
interface LocationDao {

    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(locations: List<LocationEntity>)

    @Query("SELECT * FROM locations WHERE tags LIKE '%' || :tag || '%'")
    suspend fun getByTag(tag: String): List<LocationEntity>

    @Query("SELECT DISTINCT tags FROM locations")
    suspend fun getDistinctTags(): List<String>
}
