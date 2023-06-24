package com.rie.alkisah.database.Service

import com.rie.alkisah.database.Response.FileUploadR
import com.rie.alkisah.database.Response.LoginR
import com.rie.alkisah.database.Response.RegisterR
import com.rie.alkisah.database.Response.StoryAPIR
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
//Noor Saputri
interface MrApiS {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterR

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginR

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") header: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int? = null
    ): StoryAPIR

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): FileUploadR

    @Multipart
    @POST("/v1/stories/guest")
    suspend fun uploadStoryAsGuest(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): FileUploadR

    @GET("stories")
    suspend fun getLocationUsers(
        @Header("Authorization") header: String,
        @Query("location") location: Int,
    ): StoryAPIR
}