package np.com.pedrolborges.quizonline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, // ID gerado automaticamente pelo Room
    var quizTitle: String = "",
    var scoreText: String = "",
    var percentage: Int = 0,
    var date: String = "" // Mudamos de Timestamp para String pra facilitar a exibição
) {
    // Construtor vazio necessário para o Firebase
    constructor() : this(0, "", "", 0, "")
}