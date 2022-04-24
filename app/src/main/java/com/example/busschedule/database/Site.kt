package com.example.busschedule.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class Site (
    @PrimaryKey(autoGenerate = true) val siteId: Int,
    @NonNull @ColumnInfo(name="site_name") val siteName: String,
    @NonNull @ColumnInfo(name="arrondissement") val arrondissement: Int,
    @ColumnInfo(name="url") val url: String?,
    @ColumnInfo(name="img_file") val imgFile: String?,
    @ColumnInfo(name="notes") val notes: String?,

    )