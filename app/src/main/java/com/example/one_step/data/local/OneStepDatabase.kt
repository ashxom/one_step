package com.example.one_step.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [GuideDocumentEntity::class, ActionEntity::class], version = 2, exportSchema = false)
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
            ).addMigrations(MIGRATION_1_2).build().also { instance = it }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE guide_actions ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE guide_documents ADD COLUMN deadlineBadge TEXT")
                database.execSQL("ALTER TABLE guide_documents ADD COLUMN deadlineDescription TEXT")
                database.execSQL("ALTER TABLE guide_documents ADD COLUMN locationDescription TEXT")
                database.execSQL("ALTER TABLE guide_documents ADD COLUMN tripTitle TEXT")
                database.execSQL("ALTER TABLE guide_documents ADD COLUMN targetGrade TEXT")
                database.execSQL("ALTER TABLE guide_documents ADD COLUMN phoneLabel TEXT")
                database.execSQL("ALTER TABLE guide_documents ADD COLUMN encouragement TEXT")
            }
        }
    }
}
