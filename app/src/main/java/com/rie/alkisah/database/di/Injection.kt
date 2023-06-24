package com.rie.alkisah.database.di

import MrPref
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rie.alkisah.database.Service.MrApiC
import com.rie.alkisah.database.data.MrStoryRepository
import com.rie.alkisah.database.db.MrStoryDatabase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "store")
object Injection {
    fun provideRepository(context: Context): MrStoryRepository {
        val storyDatabase = MrStoryDatabase.getDatabase(context)
        val preferences = MrPref.getInstance(context.dataStore)
        val apiService = MrApiC.getApiS()
        return MrStoryRepository(storyDatabase, apiService,preferences)
    }
}