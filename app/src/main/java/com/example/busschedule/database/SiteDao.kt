package com.example.busschedule.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @Query("SELECT * FROM site WHERE arrondissement = :arrondissement ORDER BY site_name")
    fun getSitesByArrondissement(arrondissement: Int): Flow<List<Site>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSite(site: Site)

    @Update
    suspend fun updateSite(site: Site)

    @Delete
    suspend fun delete(site: Site)
}