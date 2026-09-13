package com.example.one_step.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GuideDocumentEntity::class, ActionEntity::class], version = 1, exportSchema = false)
abstract class OneStepDatabase : RoomDatabase() {
    abstract fun guideDocumentDao(): GuideDocumentDao

    companion object {
        @Volatile
        private var instance: OneStepDatabase? = null

        fun getInstance(context: Context): OneStepDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                OneStepDatabase::class.java,
                "one_step.db",
            ).build().also { instance = it }
        }
    }
}
