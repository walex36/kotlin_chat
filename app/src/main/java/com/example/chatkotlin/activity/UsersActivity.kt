package com.example.chatkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatkotlin.R
import com.example.chatkotlin.adapter.UserAdapter
import com.example.chatkotlin.databinding.ActivityUsersBinding
import com.example.chatkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("users")

        getUserLoged()

        binding.userRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.imgBack.setOnClickListener {
            auth.signOut()
            val signInPage = Intent(this@UsersActivity, SignInActivity::class.java)
            startActivity(signInPage)
            finish()
        }

        binding.userImage.setOnClickListener {
            val profilePage = Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(profilePage)
        }


        getUserList()
    }

    private fun getUserLoged() {
        dbRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userSnapshot = snapshot.getValue(User::class.java)
                if (userSnapshot != null) {
                    Glide.with(this@UsersActivity).load(userSnapshot.profileImage)
                        .placeholder(R.drawable.profile_image).into(binding.userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getUserList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val dbRef = FirebaseDatabase.getInstance().getReference("users")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)!!

                    if (user.userId != firebase.uid) {
                        userList.add(user)
                    }
                }

                val userAdapter = UserAdapter(this@UsersActivity, userList)
                binding.userRecyclerView.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}