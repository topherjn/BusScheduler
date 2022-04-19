package com.example.busschedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busschedule.database.Site
import com.example.busschedule.database.SiteDao

class SiteViewModel(private val siteDao: SiteDao): ViewModel() {

    fun getSites(arrondissement: Int): List<Site> = siteDao.getSitesByArrondissement(arrondissement)
}

class SiteViewModelFactory(private val siteDao: SiteDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SiteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SiteViewModel(siteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}