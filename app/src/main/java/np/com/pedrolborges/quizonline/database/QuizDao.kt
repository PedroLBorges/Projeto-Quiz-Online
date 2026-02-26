package np.com.pedrolborges.quizonline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes")
    suspend fun getAllQuizzes(): List<QuizModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quizzes: List<QuizModel>)
}