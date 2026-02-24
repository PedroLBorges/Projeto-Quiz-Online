// app/src/main/java/np/com/pedrolborges/quizonline/HistoryActivity.kt
package np.com.pedrolborges.quizonline

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import np.com.pedrolborges.quizonline.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private var historyList: MutableList<HistoryModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchHistoryData()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(historyList)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun fetchHistoryData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("history")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Mostra os mais recentes primeiro
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val historyEntry = document.toObject(HistoryModel::class.java)
                    historyList.add(historyEntry)
                }
                historyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("HISTORY_FETCH", "Error getting documents: ", exception)
                }
        }
}
