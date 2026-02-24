// app/src/main/java/np/com/pedrolborges/quizonline/RegisterActivity.kt
package np.com.pedrolborges.quizonline

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import np.com.pedrolborges.quizonline.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa a instância do Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditTextRegister.text.toString()
            val password = binding.passwordEditTextRegister.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cria o usuário no Firebase
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Cadastro bem-sucedido, navega para a tela principal
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        // Se o cadastro falhar, exibe uma mensagem de erro
                        // (Ex: o email já está em uso, a senha é fraca, etc.)
                        Toast.makeText(this, "Falha no cadastro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.goToLoginTextView.setOnClickListener {
            finish()
           }
        }
}
