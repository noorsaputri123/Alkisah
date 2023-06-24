package com.rie.alkisah.database.Response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rie.alkisah.database.Response.MrApiResponse
import kotlinx.parcelize.Parcelize


data class StoryAPIR(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("listStory")

    val listStory: ArrayList<ListStoryItem>?
)

@Parcelize
data class ListStoryItem(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("lon")
    val lon: Double
) : Parcelable

data class FileUploadResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)