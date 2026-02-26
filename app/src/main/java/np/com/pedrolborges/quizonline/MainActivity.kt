package np.com.pedrolborges.quizonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import np.com.pedrolborges.quizonline.databinding.ActivityMainBinding
import androidx.room.Room
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mainQuizList : MutableList<QuizModel>
    lateinit var quizListAdapter: QuizListAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o banco de dados
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "quiz-db"
        ).build()

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
        // NOVA LÓGICA DE DECISÃO AQUI:
        if (isNetworkAvailable()) {
            // Tem internet? Tenta sincronizar com a nuvem e atualizar o banco
            getDataFromFirebase()
        } else {
            // Sem internet? Nem tenta chamar o Firebase, vai direto pro banco local!
            loadFromLocal()
        }
    }

    private fun setupRecyclerView(){
        binding.mainProgressBar.visibility = View.GONE
        quizListAdapter = QuizListAdapter(mainQuizList)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.adapter = quizListAdapter
    }

    private fun getDataFromFirebase() {
        binding.mainProgressBar.visibility = View.VISIBLE

        FirebaseDatabase.getInstance().reference
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val listFromFirebase = mutableListOf<QuizModel>()
                    for (snapshot in dataSnapshot.children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            // FORÇA O ID DO NÓ DO FIREBASE PARA O ROOM NÃO SE PERDER
                            quizModel.id = snapshot.key ?: ""
                            listFromFirebase.add(quizModel)
                        }
                    }

                    lifecycleScope.launch(Dispatchers.IO) {
                        database.quizDao().insertAll(listFromFirebase) // Salva
                        val localData = database.quizDao().getAllQuizzes() // Lê

                        withContext(Dispatchers.Main) {
                            binding.mainProgressBar.visibility = View.GONE
                            mainQuizList.clear()
                            mainQuizList.addAll(localData)
                            setupRecyclerView()
                        }
                    }
                } else {
                    loadFromLocal()
                }
            }
            .addOnFailureListener {
                // Se o Firebase falhar (sem internet), avisa e carrega local
                loadFromLocal()
            }
    }

    private fun loadFromLocal() {
        lifecycleScope.launch(Dispatchers.IO) {
            val localData = database.quizDao().getAllQuizzes()

            withContext(Dispatchers.Main) {
                binding.mainProgressBar.visibility = View.GONE

                // Um aviso visual para você saber se o Room está vazio ou se achou os dados
                if(localData.isEmpty()){
                    android.widget.Toast.makeText(this@MainActivity, "Nenhum quiz salvo no celular!", android.widget.Toast.LENGTH_LONG).show()
                } else {
                    android.widget.Toast.makeText(this@MainActivity, "Modo Offline: Carregando banco local!", android.widget.Toast.LENGTH_SHORT).show()
                }

                mainQuizList.clear()
                mainQuizList.addAll(localData)
                setupRecyclerView()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
