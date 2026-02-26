package np.com.pedrolborges.quizonline

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// ATENÇÃO: Adicionamos o HistoryModel na lista e mudamos a versão para 2
@Database(entities = [QuizModel::class, HistoryModel::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
    abstract fun historyDao(): HistoryDao // Adicionamos a conexão com o Histórico
}