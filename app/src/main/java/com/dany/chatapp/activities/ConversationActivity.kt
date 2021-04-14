package com.dany.chatapp.activities

import adapters.ConversationAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dany.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_conversation.*
import util.Message

class ConversationActivity : AppCompatActivity() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val conversationAdapter = ConversationAdapter(arrayListOf(), userId)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        messagesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }
        conversationAdapter.addMessage(Message(userId, "hi!", 2))
        conversationAdapter.addMessage(Message("Dany", "How are you!", 3))
        conversationAdapter.addMessage(Message(userId, "hi!", 3))
        conversationAdapter.addMessage(Message("Dany", "How are you!", 4))
    }

    fun onSend(v: View){

    }

    companion object{
        fun newIntent(context: Context?): Intent{
            val intent = Intent(context, ConversationActivity::class.java)
            return intent
        }
    }
}