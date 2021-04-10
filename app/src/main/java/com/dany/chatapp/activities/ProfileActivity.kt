package com.dany.chatapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StreamDownloadTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_profile.progressLayout
import util.*
import java.util.Objects.toString

class ProfileActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var imageUrl: String? = null

    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (userId.isNullOrEmpty()) {
            finish()
        }
        // Intercept all clicks that the user makes on the process layout
        progressLayout.setOnTouchListener { v, event -> true }

        // If I want whatever application the user has that picks an image of any type:
        // (This is an android way of retrieving the images)
        photoIV.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            // When the user clicks on the photo, I'll start an activity
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

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

                imageUrl = user?.imageUrl

                nameETP.setText(user?.name, TextView.BufferType.EDITABLE)
                phoneETP.setText(user?.phone, TextView.BufferType.EDITABLE)
                emailETP.setText(user?.email, TextView.BufferType.EDITABLE)

                if (imageUrl != null) {
                    populateImage(this, user?.imageUrl, photoIV, R.drawable.default_user)
                }

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
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Update failed!", Toast.LENGTH_LONG).show()
                progressLayout.visibility = View.GONE
            }
    }

    //This function deletes the profile and shows a dialog asking the user if their want to delete it
    fun onDelete(v: View) {
        progressLayout.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Delete account")
            .setMessage("This will delete your profile information. Are you sure?")
            .setPositiveButton("Yes") { dialog, which ->
                Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show()

                firebaseDB.collection(DATA_USERS).document(userId!!)
                    .delete() // Deletes the user from DB
                firebaseStorage.child(DATA_IMAGES).child(userId)
                    .delete() // Deletes the profile's picture
                firebaseAuth.currentUser?.delete() // Deletes the user account from the authentication

                    ?.addOnSuccessListener {
                        finish()
                    }
                    ?.addOnSuccessListener {
                        finish()
                    }
            }
            .setNegativeButton("No") { dialog, which ->
                progressLayout.visibility = View.GONE
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if the activity result has finished successfully and the request was the one I wanted
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            // I'm going to store that image data
            storeImage(data?.data)
        }
    }

    // Upload the image and update to the database
    private fun storeImage(imageUri: Uri?) {

        if (imageUri != null) {
            Toast.makeText(this, "Uploading the image...", Toast.LENGTH_SHORT).show()
            progressLayout.visibility = View.VISIBLE

            val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!)

            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { taskSnapshot ->
                            val url: String = taskSnapshot.toString()
                            firebaseDB.collection(DATA_USERS)
                                .document(userId)
                                .update(DATA_USER_IMAGE_URL, url)
                                .addOnSuccessListener {
                                    imageUrl = url
                                    populateImage(this, imageUrl, photoIV, R.drawable.default_user)
                                }
                            progressLayout.visibility = View.GONE
                        }
                        .addOnFailureListener {
                            onUploadFailure()
                        }
                }
                .addOnFailureListener {
                    onUploadFailure()
                }
        }
    }

    private fun onUploadFailure() {
        Toast.makeText(
            this,
            "Image upload failed. Please try again",
            Toast.LENGTH_SHORT
        ).show()
        progressLayout.visibility = View.GONE

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }

}