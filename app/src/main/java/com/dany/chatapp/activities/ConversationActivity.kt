package com.dany.chatapp.activities

import adapters.ConversationAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dany.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_conversation.*
import util.*

class ConversationActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val conversationAdapter = ConversationAdapter(arrayListOf(), userId)

    // Define the parameters of the companion object
    private var chatId: String? = null
    private var imageUrl: String? = null
    private var otherUserId: String? = null
    private var chatName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        // Retrieve and use the parameters defined in the companion object
        chatId = intent.extras?.getString(PARAM_CHAT_ID)
        imageUrl = intent.extras?.getString(PARAM_IMAGE_URL)
        otherUserId = intent.extras?.getString(PARAM_OTHER_USER_ID)
        chatName = intent.extras?.getString(PARAM_CHAT_NAME)

        if (chatId.isNullOrEmpty() || userId.isNullOrEmpty()) {
            Toast.makeText(this, "Chat room error", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Populate my layout top screen activity_conversation
        topNameTV.text = chatName
        populateImage(this, imageUrl, topPhotoIV, R.drawable.default_user)

        messagesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }

        firebaseDB.collection(DATA_CHATS)
            .document(chatId!!)
            .collection(DATA_CHAT_MESSAGES)
            .orderBy(DATA_CHAT_MESSAGE_TIME)

            // The snapshot listener will allow me to connect to the db constantly.
            // All the messages that come into the db will automatically arrive here
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    firebaseFirestoreException.printStackTrace()
                    return@addSnapshotListener
                } else {
                    if (querySnapshot != null) {
                        for (change: DocumentChange in querySnapshot.documentChanges) {
                            when (change.type) {
                                // If the change has been added
                                DocumentChange.Type.ADDED -> {
                                    val message = change.document.toObject(Message::class.java)
                                    if (message != null) {
                                        conversationAdapter.addMessage(message)
                                        messagesRV.post {
                                            // Scrolls my recycler view to the last message
                                            messagesRV.smoothScrollToPosition(conversationAdapter.itemCount - 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    fun onSend(v: View) {
        if (!messageET.text.isNullOrEmpty()) {

            val message = Message(userId, messageET.text.toString(), System.currentTimeMillis())
            firebaseDB.collection(DATA_CHATS)
                .document(chatId!!)
                .collection(DATA_CHAT_MESSAGES)
                .document()
                .set(message)
            messageET.setText("", TextView.BufferType.EDITABLE)
        }

    }

    companion object {

        private val PARAM_CHAT_ID = "Chat id"
        private val PARAM_IMAGE_URL = "Image url"
        private val PARAM_OTHER_USER_ID = "Other user id"
        private val PARAM_CHAT_NAME = "Chat name"

        fun newIntent(
            context: Context?, chatId: String?, imageUrl: String?, otherUserId: String?,
            chatName: String?
        ): Intent {
            val intent = Intent(context, ConversationActivity::class.java)

            // Pass the parameters to the activity
            intent.putExtra(PARAM_CHAT_ID, chatId)
            intent.putExtra(PARAM_IMAGE_URL, imageUrl)
            intent.putExtra(PARAM_OTHER_USER_ID, otherUserId)
            intent.putExtra(PARAM_CHAT_NAME, chatName)
            return intent
        }
    }
}