package com.example.busschedule.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @Query("SELECT * FROM site WHERE arrondissement = :arrondissement ORDER BY site_name")
    fun getSitesByArrondissement(arrondissement: Int): Flow<List<Site>>
}