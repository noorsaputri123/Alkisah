package com.rie.alkisah.database.Response

import com.google.gson.annotations.SerializedName

data class LoginR(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult?
)