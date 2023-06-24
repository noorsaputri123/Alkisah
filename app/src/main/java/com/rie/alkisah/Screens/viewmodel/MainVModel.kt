package com.rie.alkisah.Screens.viewmodel

import MrPref
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rie.alkisah.database.Service.MrApiC
import com.rie.alkisah.database.Response.StoryAPIR
import com.rie.alkisah.database.data.MrStoryRepository
import com.rie.alkisah.database.db.MrKisahResRoom
import com.rie.alkisah.model.MrUser
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
//Noor Saputri
class MainVModel (private val repository: MrStoryRepository) : ViewModel() {

    fun getStory() : LiveData<PagingData<MrKisahResRoom>> =
        repository.getStory().cachedIn(viewModelScope)

    fun getUser(): LiveData<MrUser> {
        return repository.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}