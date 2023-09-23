package com.example.chatkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var firebaseUser: FirebaseUser? = null
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            val homePage = Intent(this, UsersActivity::class.java)
            startActivity(homePage)
        }

        binding.signinBtnSignin.setOnClickListener {
            val email = binding.signinEmail.text.toString()
            val password = binding.signinPassword.text.toString()

            if (email.isEmpty()) {
                binding.signinEmail.error = "Email requerido"
            }

            if (password.isEmpty()) {
                binding.signinPassword.error = "Senha requerida"
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            }
        }

        binding.signinBtnSignup.setOnClickListener {
            val signupPage = Intent(this, SignUpActivity::class.java)
            startActivity(signupPage)
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid

                    dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = dbRef.child("userName").toString()
                    hashMap["profileImage"] = dbRef.child("profileImage").toString()

                    val homePage = Intent(this, UsersActivity::class.java)
                    startActivity(homePage)
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao logar", Toast.LENGTH_SHORT).show()
                }
            }
    }
}