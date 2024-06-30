package com.example.cse_study_and_learn_application.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QuizStats::class], version = 1, exportSchema = false)
abstract class QuizDatabase : RoomDatabase() {

    abstract val quizStatsDao: QuizStatsDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getInstance(context: Context): QuizDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        QuizDatabase::class.java,
                        "quiz_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
