package com.example.chatkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signupBtnSignup.setOnClickListener {
            val userName = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirmPassword.text.toString()

            if (userName.isEmpty()) {
                binding.signupName.error = "Nome requerido"
            }

            if (email.isEmpty()) {
                binding.signupEmail.error = "Email requerido"
            }

            if (password.isEmpty()) {
                binding.signupPassword.error = "Senha requerida"
            }

            if (confirmPassword.isEmpty()) {
                binding.signupConfirmPassword.error = "Confirmar Senha requerida"
            }

            if (password != confirmPassword) {
                binding.signupConfirmPassword.error = "Senhas não conferem"
            }

            if (password == confirmPassword && userName.isNotEmpty() && email.isNotEmpty()
                && password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                registerUser(userName, email, password)
            } else {
                Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupBtnReturn.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid

                    dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = userName
                    hashMap["profileImage"] = ""

                    dbRef.setValue(hashMap).addOnCompleteListener(this) { set ->
                        if (set.isSuccessful) {
                            val intent = Intent(this, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
    }
}