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
            loadFromLocal()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("history")
            .get()
            .addOnSuccessListener { documents ->
                val listFromFirebase = mutableListOf<HistoryModel>()
                for (document in documents) {
                    try {
                        val historyEntry = document.toObject(HistoryModel::class.java)
                        listFromFirebase.add(historyEntry)
                    } catch (e: Exception) {
                        Log.e("HISTORY", "Ignorando item antigo corrompido")
                    }
                }

                if (listFromFirebase.isNotEmpty()) {
                    // Usuário com histórico: atualiza a tela e salva offline
                    val listaOrdenada = listFromFirebase.reversed()
                    historyAdapter.updateData(listaOrdenada)
                    sincronizarComBancoLocal(listaOrdenada)
                } else {
                    // Usuário NOVO (nuvem vazia): Limpa a tela e zera o banco offline!
                    historyAdapter.updateData(emptyList())
                    sincronizarComBancoLocal(emptyList())
                }
            }
            .addOnFailureListener {
                // Sem internet: Puxa o offline
                loadFromLocal()
            }
    }

    // Cole esta nova função logo abaixo na sua HistoryActivity para fazer o Room receber os dados da nuvem
    private fun sincronizarComBancoLocal(listaDaNuvem: List<HistoryModel>) {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "quiz-db")
            .fallbackToDestructiveMigration().build()

        lifecycleScope.launch(Dispatchers.IO) {
            db.historyDao().deleteAllHistory() // Limpa os vestígios locais antigos
            for (item in listaDaNuvem) {
                db.historyDao().insertHistory(item) // Salva os dados oficiais puxados
            }
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