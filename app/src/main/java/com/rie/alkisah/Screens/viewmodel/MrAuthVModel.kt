package com.rie.alkisah.Screens.viewmodel

import MrPref
import android.util.Log
import androidx.lifecycle.*
import com.rie.alkisah.database.Response.LoginR
import com.rie.alkisah.database.Service.MrApiC
import com.rie.alkisah.database.Response.RegisterR
import com.rie.alkisah.database.data.MrStoryRepository
import com.rie.alkisah.model.MrUser
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MrAuthVModel (private val repository: MrStoryRepository) : ViewModel() {

    fun doingLogin(email: String, password: String) = repository.doingLogin(email, password)
    fun saveUser(user: MrUser) {
        viewModelScope.launch {
            repository.saveUserData(user)
        }
    }
    fun doingRegister(name: String, email: String, password: String) =
        repository.doingRegister(name, email, password)

    fun getUser(): LiveData<MrUser> {
        return repository.getUser()
    }
}