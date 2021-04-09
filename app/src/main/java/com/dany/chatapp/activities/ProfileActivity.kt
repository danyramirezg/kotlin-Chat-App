package com.dany.chatapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dany.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_profile.progressLayout
import util.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (userId.isNullOrEmpty()) {
            finish()
        }
        // Intercept all clicks that the user makes on the process layout
        progressLayout.setOnTouchListener { v, event -> true }

        populateInfo()
    }

    // This function will populate the interface (Profile)
    private fun populateInfo() {

        progressLayout.visibility = View.GONE
        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)

                nameETP.setText(user?.name, TextView.BufferType.EDITABLE)
                phoneETP.setText(user?.phone, TextView.BufferType.EDITABLE)
                emailETP.setText(user?.email, TextView.BufferType.EDITABLE)

                progressLayout.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }


    fun onApply(v: View) {
        // Update the information from the database:

        progressLayout.visibility = View.GONE

        val name = nameETP.text.toString().trim()
        val phone = phoneETP.text.toString().trim()
        val email = emailETP.text.toString().trim()

        val map = HashMap<String, Any>()
        map[DATA_USER_NAME] = name
        map[DATA_USER_PHONE] = phone
        map[DATA_USER_EMAIL] = email

        firebaseDB.collection(DATA_USERS)
            .document(userId!!)
            .update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Changes applied successful!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener{ e ->
                e.printStackTrace()
                Toast.makeText(this, "Update failed!", Toast.LENGTH_LONG).show()
                progressLayout.visibility = View.GONE
            }
    }

    fun onDelete(v: View) {
        progressLayout.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Delete account")
            .setMessage("This will delete your profile information. Are you sure?")
            .setPositiveButton("Yes"){dialog, which ->
                Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show()
                firebaseDB.collection(DATA_USERS).document(userId!!).delete()
                finish()
            }
            .setNegativeButton("No"){dialog, which ->
                progressLayout.visibility = View.GONE
            }
            .show()

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }

}