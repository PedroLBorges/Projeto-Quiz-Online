package np.com.pedrolborges.quizonline

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import np.com.pedrolborges.quizonline.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchHistoryData()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(emptyList()) // Começa vazio
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun fetchHistoryData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            // Se não estiver logado, puxa direto do celular
            loadFromLocal()
            return
        }

        // 1. TENTA BUSCAR DA NUVEM (FIREBASE)
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("history")
            .get() // Removido o orderBy antigo que estava quebrando a busca
            .addOnSuccessListener { documents ->
                val listFromFirebase = mutableListOf<HistoryModel>()
                for (document in documents) {
                    val historyEntry = document.toObject(HistoryModel::class.java)
                    listFromFirebase.add(historyEntry)
                }

                if (listFromFirebase.isNotEmpty()) {
                    // Atualiza a tela com os dados da nuvem (invertemos para o mais recente ficar no topo)
                    historyAdapter.updateData(listFromFirebase.reversed())
                } else {
                    // Se o Firebase estiver vazio, tenta achar algo no celular
                    loadFromLocal()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("HISTORY_FETCH", "Erro ao buscar do Firebase. Buscando local...", exception)
                // 2. SE FALHAR (SEM INTERNET), BUSCA DO BANCO LOCAL ROOM
                loadFromLocal()
            }
    }

    private fun loadFromLocal() {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "quiz-db").build()

        lifecycleScope.launch(Dispatchers.IO) {
            // A nossa Query no DAO já puxa do mais recente pro mais antigo
            val localHistory = db.historyDao().getAllHistory()

            withContext(Dispatchers.Main) {
                if (localHistory.isNotEmpty()) {
                    Toast.makeText(this@HistoryActivity, "Mostrando histórico offline", Toast.LENGTH_SHORT).show()
                    historyAdapter.updateData(localHistory)
                } else {
                    Toast.makeText(this@HistoryActivity, "Nenhum histórico encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}