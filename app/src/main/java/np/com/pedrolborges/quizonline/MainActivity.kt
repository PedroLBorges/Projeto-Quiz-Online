package np.com.pedrolborges.quizonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import np.com.pedrolborges.quizonline.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mainQuizList : MutableList<QuizModel>
    lateinit var quizListAdapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.historyBtn.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // LÓGICA DO BOTÃO SAIR
        binding.logoutBtn.setOnClickListener {
            // Faz o logout do Firebase
            FirebaseAuth.getInstance().signOut()

            // Redireciona para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            // Limpa todas as telas anteriores para que o usuário não possa "voltar" para o app logado
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        mainQuizList = mutableListOf()
        getDataFromFirebase()
    }

    private fun setupRecyclerView(){
        binding.mainProgressBar.visibility = View.GONE
        quizListAdapter = QuizListAdapter(mainQuizList)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.adapter = quizListAdapter
    }

    private fun getDataFromFirebase(){
        binding.mainProgressBar.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference
            .get()
            .addOnSuccessListener { dataSnapshot->
                if(dataSnapshot.exists()){
                    for (snapshot in dataSnapshot.children){
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            mainQuizList.add(quizModel)
                        }
                    }
                }
                setupRecyclerView()
                }
        }
}
