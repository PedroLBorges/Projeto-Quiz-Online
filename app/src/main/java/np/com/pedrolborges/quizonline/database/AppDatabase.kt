package np.com.pedrolborges.quizonline

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [QuizModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Avisando o Room para usar o tradutor
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}