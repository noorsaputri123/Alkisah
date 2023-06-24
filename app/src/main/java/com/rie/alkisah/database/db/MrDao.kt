package com.rie.alkisah.database.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MrDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<MrKisahResRoom>)

    @Query("SELECT * FROM Mrstory")
    fun getAllStory(): PagingSource<Int, MrKisahResRoom>

    @Query("DELETE FROM Mrstory")
    suspend fun deleteAll()
}