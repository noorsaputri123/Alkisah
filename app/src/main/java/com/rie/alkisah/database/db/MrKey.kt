package com.rie.alkisah.database.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Mrkey")
data class MrKey (
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)