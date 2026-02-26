package np.com.pedrolborges.quizonline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    // Busca o histórico, do mais recente para o mais antigo
    @Query("SELECT * FROM history ORDER BY id DESC")
    suspend fun getAllHistory(): List<HistoryModel>

    // Insere o resultado de uma partida no banco local
    @Insert
    suspend fun insertHistory(history: HistoryModel)
}