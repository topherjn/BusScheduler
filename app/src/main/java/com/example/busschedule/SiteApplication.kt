package com.example.busschedule

import android.app.Application
import com.example.busschedule.database.AppDatabase

class SiteApplication: Application() {
    val database: AppDatabase by lazy {AppDatabase.getDatabase(this)}
}