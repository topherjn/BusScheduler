package com.example.busschedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.busschedule.database.Site
import com.example.busschedule.database.SiteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SiteViewModel(private val siteDao: SiteDao): ViewModel() {

    fun getSites(arrondissement: Int): Flow<List<Site>> = siteDao.getSitesByArrondissement(arrondissement)

    fun getSite(siteId: Int): Flow<List<Site>> = siteDao.getSiteBySiteId(siteId)

    fun insertSite(
        siteId: Int,
        siteName: String,
        arrondissement: Int,
        notes: String,
        imageFile:String,
        url: String
    ) {
        val site = Site(
            siteId = siteId,
            siteName = siteName,
            arrondissement = arrondissement,
            imgFile = imageFile,
            notes = notes,
            url = url)

        viewModelScope.launch(Dispatchers.IO) {siteDao.insertSite(site)}
    }
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