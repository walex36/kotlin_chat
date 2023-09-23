package com.example.chatkotlin.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chatkotlin.R
import com.example.chatkotlin.databinding.ActivityProfileBinding
import com.example.chatkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var user: User? = null

    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference("profile")
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        dbRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                if (user != null) {
                    binding.editUserName.setText(user!!.userName)

                    if (user!!.profileImage == "") {
                        binding.userImage.setImageResource(R.drawable.profile_image)
                    } else {
                        Glide.with(this@ProfileActivity).load(user!!.profileImage)
                            .placeholder(R.drawable.profile_image).into(binding.userImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.userImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            chooseImage.launch(pickImg)
        }

        binding.profileBtnSave.setOnClickListener {
            uploudImage(user!!.userId, binding.progressBar)
        }
    }

    private val chooseImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    filePath = it.data!!.data

                    Glide.with(this@ProfileActivity).load(filePath)
                        .placeholder(R.drawable.profile_image).into(binding.userImage)

                    binding.profileBtnSave.visibility = View.VISIBLE
                }
            }
        }

    private fun uploudImage(userId: String, progressBar: ProgressBar) {
        if (filePath != null) {
            progressBar.visibility = View.VISIBLE

            var ref: StorageReference = storageRef.child(userId)
            ref.putFile(filePath!!).addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    applicationContext,
                    "Falha ao enviar: " + it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val hashMap: HashMap<String, String> = HashMap()

                        hashMap["userName"] = binding.editUserName.text.toString()
                        hashMap["profileImage"] = uri.toString()

                        dbRef.updateChildren(hashMap as Map<String, Any>)

                        progressBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "Enviado", Toast.LENGTH_SHORT).show()
                        binding.profileBtnSave.visibility = View.GONE
                    }
                }

            }

        }
    }

}