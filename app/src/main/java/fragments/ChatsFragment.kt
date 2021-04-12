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
import kotlinx.android.synthetic.main.fragment_chats.*
import listeners.ChatClickListener

class ChatsFragment : Fragment(), ChatClickListener {

    private var chatsAdapter = ChatsAdapter(arrayListOf())

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

        var chatList = arrayListOf("chat 1", "chat 2", "chat 1", "chat 2", "chat 1", "chat 2")

        chatsAdapter.updateChat(chatList)
    }

    override fun onChatClicked(
        name: String?,
        otherUserId: String?,
        chatImageUrl: String?,
        chatName: String?
    ) {
        Toast.makeText(context, "$name clicked", Toast.LENGTH_SHORT).show()
    }
}