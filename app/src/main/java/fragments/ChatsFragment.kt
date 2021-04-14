package fragments

import adapters.ChatsAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dany.chatapp.R
import com.dany.chatapp.activities.ConversationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.android.synthetic.main.fragment_chats.*
import listeners.ChatClickListener
import listeners.FailureCallback
import util.Chat
import util.DATA_CHATS
import util.DATA_USERS
import util.DATA_USER_CHATS

class ChatsFragment : Fragment(), ChatClickListener {

    private var chatsAdapter = ChatsAdapter(arrayListOf())
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var failureCallback: FailureCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (userId.isNullOrEmpty()) {
            failureCallback?.onUserError()

        }
    }

    fun setFailureCallbackListener(listener: FailureCallback) {
        failureCallback = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    // Allows me to implement the chatRV (Recycler view) functionality
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsAdapter.setOnItemClickListener(this)
        chatsRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        // I have a kind of listener that always is connected to the database and whenever there's
        // a change we refresh the database
        firebaseDB.collection(DATA_USERS).document(userId!!).addSnapshotListener { documentSnapshot,
                                                                                   firebaseFirestoneException ->
            if (firebaseFirestoneException == null) {
                refreshChats()
            }
        }

//        In the beginning to test the views in the Recycler view:
//        var chatList = arrayListOf("chat 1", "chat 2", "chat 1", "chat 2", "chat 1", "chat 2")
//        chatsAdapter.updateChat(chatList)
    }

    // Method to show up on the screen:
    private fun refreshChats() {
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.contains(DATA_USER_CHATS)) {
                    val partners: Any? = documentSnapshot[DATA_USER_CHATS]
                    val chats: ArrayList<String> = arrayListOf<String>()
                    for (partner in (partners as HashMap<*, *>).keys) {
                        if (partners[partner] != null) {
                            chats.add((partners[partner] as String?)!!)
                        }
                    }
                    chatsAdapter.updateChat(chats)
                }
            }
    }

    // Checking if the user has any chats then I simply convert that to a user document map
    fun newChat(partnerId: String) {
        // This information is for my user
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener { userDocument ->
                val userChatPartners: HashMap<String, String> = hashMapOf<String, String>()

                if (userDocument[DATA_USER_CHATS] != null && userDocument[DATA_USER_CHATS] is HashMap<*, *>) {
                    val userDocumentMap: HashMap<String, String> =
                        userDocument[DATA_USER_CHATS] as HashMap<String, String>
                    // Need to check if it contains my partner information:
                    if (userDocumentMap.containsKey(partnerId)) {
                        return@addOnSuccessListener
                    } else {
                        userChatPartners.putAll(userDocumentMap) //list of my chat partners (I chatted with)
                    }
                }
                // This information is for the other user (for the person I'm chatting with):
                firebaseDB.collection(DATA_USERS)
                    .document(partnerId)
                    .get()
                    .addOnSuccessListener { partnerDocument ->
                        val partnerChatPartners: HashMap<String, String> =
                            hashMapOf<String, String>()

                        if (partnerDocument[DATA_USER_CHATS] != null && partnerDocument[DATA_USER_CHATS] is HashMap<*, *>) {
                            val partnerDocumentMap: HashMap<String, String> =
                                partnerDocument[DATA_USER_CHATS] as HashMap<String, String>
                            partnerChatPartners.putAll(partnerDocumentMap) //list of my partners chat partners (their chats)
                        }

                        // Update the database:
                        val chatParticipants: ArrayList<String> = arrayListOf(userId, partnerId)
                        val chat = Chat(chatParticipants)

                        // Create a new collection (DATA_CHATS)
                        // Chat reference generates a chat reference for me
                        val chatRef: DocumentReference =
                            firebaseDB.collection(DATA_CHATS).document()

                        // I need my userId because I need to set it to my partner and vice versa
                        val userRef: DocumentReference =
                            firebaseDB.collection(DATA_USERS).document(userId)
                        val partnerRef: DocumentReference =
                            firebaseDB.collection(DATA_USERS).document(partnerId)

                        // Create the update list of chat partner and user partners
                        userChatPartners[partnerId] = chatRef.id
                        partnerChatPartners[userId] = chatRef.id

                        // Update the database for everything at the same time
                        val batch: WriteBatch = firebaseDB.batch()
                        batch.set(chatRef, chat)
                        batch.update(userRef, DATA_USER_CHATS, userChatPartners)
                        batch.update(partnerRef, DATA_USER_CHATS, partnerChatPartners)
                        batch.commit()

                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


    override fun onChatClicked(
        chatId: String?,
        otherUserId: String?,
        chatImageUrl: String?,
        chatName: String?
    ) {
        startActivity(ConversationActivity.newIntent(context, chatId, chatImageUrl, otherUserId, chatName))
    }
}
