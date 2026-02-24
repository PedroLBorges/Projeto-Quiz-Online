// app/src/main/java/np/com/pedrolborges/quizonline/QuizActivity.kt
package np.com.pedrolborges.quizonline

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import np.com.pedrolborges.quizonline.databinding.ActivityQuizBinding
import np.com.pedrolborges.quizonline.databinding.ScoreDialogBinding

class QuizActivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        var questionModelList : List<QuestionModel> = listOf()
        var time : String = ""
    }

    lateinit var binding: ActivityQuizBinding

    var currentQuestionIndex = 0;
    var selectedAnswer = ""
    var score = 0;
    lateinit var quizTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        quizTitle = intent.getStringExtra("QUIZ_TITLE") ?: "Quiz" // Recebe o título
        loadQuestions()
        startTimer()
    }

    private fun startTimer(){
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis,1000L){
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished /1000
                val minutes = seconds/60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes,remainingSeconds)
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    private fun loadQuestions(){
        selectedAnswer = ""
        if(currentQuestionIndex == questionModelList.size){
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex+1}/ ${questionModelList.size} "
            questionProgressIndicator.progress =
                ( currentQuestionIndex.toFloat() / questionModelList.size.toFloat() * 100 ).toInt()
            questionTextview.text = questionModelList[currentQuestionIndex].question
            btn0.text = questionModelList[currentQuestionIndex].options[0]
            btn1.text = questionModelList[currentQuestionIndex].options[1]
            btn2.text = questionModelList[currentQuestionIndex].options[2]
            btn3.text = questionModelList[currentQuestionIndex].options[3]
        }
    }

    override fun onClick(view: View?) {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
        }

        val clickedBtn = view as Button
        if(clickedBtn.id==R.id.next_btn){
            if(selectedAnswer.isEmpty()){
                Toast.makeText(applicationContext,"Please select answer to continue",Toast.LENGTH_SHORT).show()
                return;
            }
            if(selectedAnswer == questionModelList[currentQuestionIndex].correct){
                score++
            }
            currentQuestionIndex++
            loadQuestions()
        } else {
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(getColor(R.color.blue))
        }
    }

    private fun finishQuiz(){
        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat() ) *100 ).toInt()
        val scoreText = "$score de $totalQuestions"

        saveResultToFirebase(scoreText, percentage)

        val dialogBinding  = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if(percentage>60){
                scoreTitle.text = "Parabéns! Você passou"
                scoreTitle.setTextColor(Color.BLUE)
            } else {
                scoreTitle.text = "Oops! Tente novamente"
                scoreTitle.setTextColor(Color.RED)
            }
            scoreSubtitle.text = "$scoreText corretas"
            finishBtn.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()
    }

    private fun saveResultToFirebase(scoreText: String, percentage: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Você precisa estar logado para salvar o histórico.", Toast.LENGTH_LONG).show()
            return
        }

        val historyEntry = HistoryModel(quizTitle, scoreText, percentage)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("history")
            .add(historyEntry)
            .addOnSuccessListener {
                Log.d("HISTORY_SAVE", "Histórico salvo com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("HISTORY_SAVE", "Erro ao salvar histórico", e)
                }
        }
}
