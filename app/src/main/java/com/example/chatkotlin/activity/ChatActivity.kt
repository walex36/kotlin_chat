package com.example.chatkotlin.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatkotlin.R
import com.example.chatkotlin.adapter.ChatAdapter
import com.example.chatkotlin.databinding.ActivityChatBinding
import com.example.chatkotlin.model.Chat
import com.example.chatkotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var userAuth: FirebaseUser
    var chatList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var friendId: String = intent.getStringExtra("userId").toString()

        friendData(friendId)
        LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        userAuth = FirebaseAuth.getInstance().currentUser!!

        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.chatBtnSend.setOnClickListener {
            var message: String = binding.chatMessage.text.toString()

            if (message.isNotEmpty()) {
                sendMessage(userAuth.uid, friendId, message)
            }
        }

        getChatUser(userAuth.uid, friendId)
    }

    private fun friendData(friendId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(friendId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                binding.tvUserName.text = user.userName
                if (user != null) {
                    Glide.with(this@ChatActivity).load(user.profileImage)
                        .placeholder(R.drawable.profile_image).into(binding.userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        dbRef = FirebaseDatabase.getInstance().reference

        val localTime: LocalDateTime = LocalDateTime.now()

        var hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        hashMap["time"] = localTime.toString()

        binding.chatMessage.setText("")

        dbRef.child("chat").push().setValue(hashMap)
    }

    private fun getChatUser(senderId: String, receiverId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("chat")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.senderId == senderId && chat!!.receiverId == receiverId ||
                        chat!!.senderId == receiverId && chat!!.receiverId == senderId
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)
                binding.chatRecyclerView.adapter = chatAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Erro ao enviar mensage", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
