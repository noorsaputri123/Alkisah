package com.rie.alkisah.Screens.viewmodel

import MrPref
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rie.alkisah.database.Response.FileUploadR
import com.rie.alkisah.database.Service.MrApiC
import com.rie.alkisah.database.data.MrStoryRepository
import com.rie.alkisah.model.MrUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Noor Saputri

class UploadVModel (private val repository: MrStoryRepository) : ViewModel() {
    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?,
                 lon: Double?) = repository.addStory(token, file, description, lat, lon)
//    fun addStoryAsGuest(file: MultipartBody.Part, description: RequestBody, lat: Double?,
//                        lon: Double?) = repository.addStoryAsGuest(file, description, lat, lon)
    fun getUser(): LiveData<MrUser> {
        return repository.getUser()
    }

}