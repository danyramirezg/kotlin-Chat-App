package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dany.chatapp.R
import com.google.api.Distribution
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import listeners.ChatClickListener
import listeners.ContactsClickListener
import util.*

class ChatsAdapter(val chats: ArrayList<String>) :
    RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    private var clickListener: ChatClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatsAdapter.ChatsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chats.size

    }

    override fun onBindViewHolder(holder: ChatsAdapter.ChatsViewHolder, position: Int) {
        holder.bind(chats[position], clickListener)

    }

    // Method to attach the click listener, this is going to be called by whichever object needs to listen to click events.
    fun setOnItemClickListener(listener: ChatClickListener) {
        clickListener = listener

        notifyDataSetChanged() // Once I update the listener I want to redo the whole list
        // It recreates the whole list to attach the right listener to the layouts
    }

    // Allows me update the list on the adapter
    fun updateChat(updatedChats: ArrayList<String>) {
        chats.clear()
        chats.addAll(updatedChats)
        notifyDataSetChanged()

    }


    class ChatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var chatIV = view.findViewById<ImageView>(R.id.chatIV)
        private var chatNameTV = view.findViewById<TextView>(R.id.chatTV)
        private var layout = view.findViewById<RelativeLayout>(R.id.chatLayout)
        private var progressLayout = view.findViewById<LinearLayout>(R.id.progressLayout)

        private val userId = FirebaseAuth.getInstance().currentUser.uid
        private val firebaseDB = FirebaseFirestore.getInstance()
        private val firebaseAuth = FirebaseAuth.getInstance().currentUser.uid

        private var partnerId: String? = null
        private var chatImageUrl: String? = null
        private var chatName: String? = null

        //Populate the list of the elements
        fun bind(chatId: String, listener: ChatClickListener?) {

            progressLayout.visibility = View.GONE
            progressLayout.setOnTouchListener { v, event -> true }

            firebaseDB.collection(DATA_CHATS).document(chatId).get()
                .addOnSuccessListener { document ->
                    val chatParticipants: Any? = document[DATA_CHAT_PARTICIPANTS]
                    if (chatParticipants != null) {
                    for (participant in chatParticipants as ArrayList<String>) {
                        if (participant != null && participant != userId) {
                            partnerId = participant
                            firebaseDB.collection(DATA_USERS)
                                .document(partnerId!!)
                                .get()
                                .addOnSuccessListener { document ->
                                    val user: User? = document.toObject(User::class.java)
                                    chatImageUrl = user?.imageUrl
                                    chatName = user?.name
                                    chatNameTV.text = user?.name
                                    populateImage(
                                        chatIV.context,
                                        user?.imageUrl,
                                        chatIV,
                                        R.drawable.default_user
                                    )
                                    progressLayout.visibility = View.GONE
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                    progressLayout.visibility = View.GONE
                                }
                        }
                    }
                }
        }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    progressLayout.visibility = View.GONE
                }
            layout.setOnClickListener {
                listener?.onChatClicked(
                    chatId,
                    partnerId,
                    chatImageUrl,
                    chatName
                )
            }
        }
    }
}