package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dany.chatapp.R
import com.dany.chatapp.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_status_update.*
import kotlinx.android.synthetic.main.item_chat.*
import kotlinx.android.synthetic.main.item_chat.progressLayout
import util.REQUEST_CODE_PHOTO
import util.populateImage


class StatusUpdateFragment : Fragment() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var imageUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_update, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // Intercept all clicks by the user. When the progress layout is visible the user cannot click on anything
        progressLayout.setOnTouchListener{ v, event -> true}
        sendStatusButton.setOnClickListener{ onUpdate()}
        context?.let { populateImage(it, imageUrl, statusIV) }

        statusLayout.setOnClickListener{
            if(isAdded){
                (activity as MainActivity).startNewActivity(REQUEST_CODE_PHOTO)
            }
        }
    }

    fun onUpdate(){

    }

    private fun onUploadFailure(){
        Toast.makeText(activity, "Image upload failed. Please try again later", Toast.LENGTH_SHORT).show()
        progressLayout.visibility = View.GONE
    }
}