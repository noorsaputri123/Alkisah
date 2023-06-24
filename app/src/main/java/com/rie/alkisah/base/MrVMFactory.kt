package com.rie.alkisah.base

import MrPref
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rie.alkisah.Screens.viewmodel.*
import com.rie.alkisah.database.data.MrStoryRepository
import com.rie.alkisah.database.di.Injection

class MrVMFactory(private val repos: MrStoryRepository) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainVModel::class.java) -> {
                MainVModel(repos) as T
            }
            modelClass.isAssignableFrom(UploadVModel::class.java) -> {
                UploadVModel(repos) as T
            }
            modelClass.isAssignableFrom(MrAuthVModel::class.java) -> {
                MrAuthVModel(repos) as T
            }
            modelClass.isAssignableFrom(MrMapsVModel::class.java) -> {
                MrMapsVModel(repos) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: MrVMFactory? = null
        fun getInstance(context: Context): MrVMFactory {
            return instance ?: synchronized(this) {
                instance ?: MrVMFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }

}
