package com.example.busschedule.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Site (
    @PrimaryKey(autoGenerate = true)
    val siteId: Int,
    @NonNull @ColumnInfo(name="site_name") val siteName: String,
    @NonNull @ColumnInfo val arrondissement: Int,
    @ColumnInfo val url: String,
    @ColumnInfo val notes: String,
    @ColumnInfo(name="img_file") val imgFile: String
)