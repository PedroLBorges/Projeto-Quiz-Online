// app/src/main/java/np/com/pedrolborges/quizonline/HistoryModel.kt
package np.com.pedrolborges.quizonline

/** Deepseek - inicio
 *
 * Prompt: Crie uma data class em Kotlin para representar um modelo de histórico de quizzes. A classe deve incluir os seguintes campos:
 * quizTitle: String com valor padrão vazio
 * scoreText: String com valor padrão vazio
 * percentage: Inteiro com valor padrão 0
 * timestamp: Timestamp do Firebase com valor padrão Timestamp.now()
 * Inclua também um construtor vazio necessário para compatibilidade com o Firebase.
 *
 */
import com.google.firebase.Timestamp

data class HistoryModel(
    val quizTitle: String = "",
    val scoreText: String = "",
    val percentage: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
) {
    // Construtor vazio necessário para o Firebase
    constructor() : this("", "", 0, Timestamp.now())
}

/** Deepseek - final */