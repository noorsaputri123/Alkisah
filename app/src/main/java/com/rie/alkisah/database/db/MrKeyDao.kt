package com.rie.alkisah.database.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MrKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MrKey>)

    @Query("SELECT * FROM Mrkey WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): MrKey?

    @Query("DELETE FROM Mrkey")
    suspend fun deleteRemoteKeys()
}