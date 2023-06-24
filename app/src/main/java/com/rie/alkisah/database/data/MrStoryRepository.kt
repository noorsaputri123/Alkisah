package com.rie.alkisah.database.data

import MrPref
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rie.alkisah.database.Response.LoginR
import com.rie.alkisah.database.Response.RegisterR
import com.rie.alkisah.database.Response.StoryAPIR
import com.rie.alkisah.database.Service.MrApiS
import com.rie.alkisah.database.db.MrKisahResRoom
import com.rie.alkisah.database.db.MrStoryDatabase
import com.rie.alkisah.model.MrUser
import com.rie.alkisah.base.Result
import com.rie.alkisah.database.Response.FileUploadR
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class MrStoryRepository (private val storyDatabase: MrStoryDatabase, private val apiService: MrApiS, private val pref: MrPref,) {

    fun getStory(): LiveData<PagingData<MrKisahResRoom>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = MrRemoteMediator(storyDatabase, apiService, pref),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun doingLogin(email: String, password: String): LiveData<Result<LoginR>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            if (e is HttpException) {
                val response = Gson().fromJson<LoginR>(e.response()?.errorBody()?.charStream(),
                    object : TypeToken<LoginR>() {}.type
                )
                emit(Result.Error(response.message ?: "Unknown error occurred"))
            } else {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun doingRegister(name: String, email: String, password: String): LiveData<Result<RegisterR>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            if (e is HttpException) {
                val response = Gson().fromJson<RegisterR>(e.response()?.errorBody()?.charStream(),
                    object : TypeToken<RegisterR>() {}.type
                )
                emit(Result.Error(response.message ?: "Unknown error occurred"))
            } else {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun addStory(token: String, imageMultipart: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?): LiveData<Result<FileUploadR>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory(token, imageMultipart, description,lat, lon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }


    fun getLocationUser(token: String): LiveData<Result<StoryAPIR>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getLocationUsers(token, 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveUserData(user:MrUser) {
        pref.saveUser(user)
    }

    fun getUser(): LiveData<MrUser> {
        return pref.getUser().asLiveData()
    }

    suspend fun logout() {
        pref.logout()
    }
}