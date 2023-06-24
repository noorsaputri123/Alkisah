package com.rie.alkisah.database.Service
//Noor Saputri
import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MrApiC {
  companion object{
   fun getApiS(): MrApiS {
    val loggingInterceptor = if(BuildConfig.DEBUG) {
     HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
     HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }
    val client = OkHttpClient.Builder()
     .addInterceptor(loggingInterceptor)
     .build()
    val retrofit = Retrofit.Builder()
     .baseUrl("https://story-api.dicoding.dev/v1/")
     .addConverterFactory(GsonConverterFactory.create())
     .client(client)
     .build()
    return retrofit.create(MrApiS::class.java)
   }
  }
 }

