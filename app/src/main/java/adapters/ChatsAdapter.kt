package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dany.chatapp.R
import listeners.ChatClickListener
import listeners.ContactsClickListener
import util.Contact
import util.populateImage

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
    fun updateChat(updatedChats: ArrayList<String>){
        chats.clear()
        chats.addAll(updatedChats)
        notifyDataSetChanged()

    }


    class ChatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var chatIV = view.findViewById<ImageView>(R.id.chatIV)
        private var chatName = view.findViewById<TextView>(R.id.chatTV)

        //Populate the list of the elements
        fun bind(chatId: String, listener: ChatClickListener?) {

            chatName.text = chatId
            populateImage(chatIV.context, "", chatIV, R.drawable.default_user)
        }
    }
}