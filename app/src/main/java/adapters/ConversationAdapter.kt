package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dany.chatapp.R
import util.Message

class ConversationAdapter (private var messages: ArrayList<Message>, val userId: String?):
    RecyclerView.Adapter<ConversationAdapter.MessagesViewHolder>(){

    companion object{
        val MESSAGE_CURRENT_USER = 1
        val MESSAGE_OTHER_USER = 2
    }

    fun addMessage(message: Message){
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationAdapter.MessagesViewHolder {

        return if(viewType == MESSAGE_CURRENT_USER){
            val mCurrentU = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_current_user_message, parent, false)
            MessagesViewHolder(mCurrentU )
        }else{
            val mOtherU = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_other_user_message, parent, false)
            MessagesViewHolder(mOtherU)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    // Message that I want to be bound to this particular layout (view)
    override fun onBindViewHolder(
        holder: ConversationAdapter.MessagesViewHolder,
        position: Int
    ) {
        holder.bind(messages[position])
    }

    override fun getItemViewType(position: Int): Int {
        //If the current message is sent by me, then I return the message_current_user
        return if(messages[position].sentby == userId){
            MESSAGE_CURRENT_USER
        }else{
            MESSAGE_OTHER_USER
        }
    }

    class MessagesViewHolder(val view: View):RecyclerView.ViewHolder(view){
        fun bind(message: Message){
            view.findViewById<TextView>(R.id.messageTV).text = message.message

        }
    }

}