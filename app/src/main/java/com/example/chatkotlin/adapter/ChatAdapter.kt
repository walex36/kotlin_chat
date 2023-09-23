package com.example.chatkotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatkotlin.R
import com.example.chatkotlin.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            ChatViewHolder(view)
        } else {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            ChatViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val formatDateTime = DateTimeFormatter.ofPattern("HH:mm")
        val chat: Chat = chatList[position]
        holder.txtChatMessage.text = chat.message
        holder.txtTemp.text = formatDateTime.format(LocalDateTime.parse(chat.time))
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtChatMessage: TextView = view.findViewById(R.id.tv_message)
        val txtTemp: TextView = view.findViewById(R.id.tv_time)
        val imgUser: CircleImageView = view.findViewById(R.id.userImage)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].senderId == firebaseUser!!.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }

}