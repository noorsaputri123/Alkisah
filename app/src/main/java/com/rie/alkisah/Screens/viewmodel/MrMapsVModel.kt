package com.rie.alkisah.Screens.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rie.alkisah.database.data.MrStoryRepository
import com.rie.alkisah.model.MrUser

class MrMapsVModel (private val repository: MrStoryRepository) : ViewModel() {
    fun getStoryLocation(token: String) = repository.getLocationUser(token)

    fun getUser(): LiveData<MrUser> {
        return repository.getUser()
    }
}