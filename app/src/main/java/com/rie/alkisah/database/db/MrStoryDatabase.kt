package com.rie.alkisah.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [MrKisahResRoom::class, MrKey::class],
    version = 2,
    exportSchema = false
)
abstract class MrStoryDatabase : RoomDatabase(){
    abstract fun storyDao(): MrDao
    abstract fun remoteKeysDao(): MrKeyDao

    companion object {
        @Volatile
        private var INSTANCE: MrStoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MrStoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MrStoryDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}