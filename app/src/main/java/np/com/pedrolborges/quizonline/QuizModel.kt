package np.com.pedrolborges.quizonline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class QuizModel(
    @PrimaryKey var id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val time: String = "",
    val questionList: List<QuestionModel> = emptyList() // A lista voltou!
) {
    // Construtor vazio exigido pelo Firebase
    constructor() : this("", "", "", "", emptyList())
}

data class QuestionModel(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correct: String = ""
) {
    // Construtor vazio exigido pelo Firebase
    constructor() : this("", emptyList(), "")
}