package com.cslu.cse_study_and_learn_application.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuizStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(quizStats: QuizStats)

    @Query("SELECT * FROM quiz_stats WHERE nickname = :nickname")
    suspend fun getStatsByNickname(nickname: String): QuizStats?

    @Query("DELETE FROM quiz_stats WHERE nickname = :nickname")
    suspend fun clear(nickname: String)
}
