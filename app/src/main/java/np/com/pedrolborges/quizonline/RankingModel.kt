package np.com.pedrolborges.quizonline

data class RankingModel(
    val name: String = "",
    val totalScore: Int = 0
) {
    // Construtor vazio para o Firebase
    constructor() : this("", 0)
}