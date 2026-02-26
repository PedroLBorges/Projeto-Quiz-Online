package np.com.pedrolborges.quizonline

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import np.com.pedrolborges.quizonline.databinding.ActivityRankingBinding

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
    private lateinit var rankingAdapter: RankingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchRanking()
    }

    private fun setupRecyclerView() {
        rankingAdapter = RankingAdapter(emptyList())
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rankingRecyclerView.adapter = rankingAdapter
    }

    private fun fetchRanking() {
        // Busca os usuários no Firebase e ordena pela maior pontuação (DESCENDING)
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(50) // Mostra só os 50 melhores
            .get()
            .addOnSuccessListener { documents ->
                val listFromFirebase = mutableListOf<RankingModel>()
                for (document in documents) {
                    val rankingEntry = document.toObject(RankingModel::class.java)
                    // Só adiciona quem tem mais de 0 pontos
                    if (rankingEntry.totalScore > 0) {
                        listFromFirebase.add(rankingEntry)
                    }
                }
                rankingAdapter.updateData(listFromFirebase)
            }
            .addOnFailureListener { exception ->
                Log.e("RANKING", "Erro ao buscar ranking", exception)
                Toast.makeText(this, "Erro ao carregar o Ranking.", Toast.LENGTH_SHORT).show()
            }
    }
}