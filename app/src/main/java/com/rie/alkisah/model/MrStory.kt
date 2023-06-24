package com.rie.alkisah.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
//Noor Saputri
@Parcelize
class MrStory (
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String
) : Parcelable