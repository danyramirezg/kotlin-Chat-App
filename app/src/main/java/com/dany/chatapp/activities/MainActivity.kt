package com.dany.chatapp.activities

import android.Manifest.permission.READ_CONTACTS
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.Placeholder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dany.chatapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fragments.ChatsFragment
import fragments.StatusFragment
import fragments.StatusUpdateFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import util.DATA_USERS
import util.DATA_USER_PHONE
import util.PERMISSIONS_REQUEST_READ_CONTACTS
import util.REQUEST_NEW_CHAT
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var mySectionPagerAdapter: SectionPagerAdapter? = null

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val statusUpdateFragment = StatusUpdateFragment()
    private val chatsFragment = ChatsFragment()
    private val statusFragment = StatusFragment()

    private var firebaseDB = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        mySectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)

        container.adapter = mySectionPagerAdapter
        // Switch into Tables (camera, chats, status)
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        resizeTabs()
        tabs.getTabAt(1)?.select()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            // Hides (in camera, status) and shows in chats the newChat button
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> fab.hide()
                    1 -> fab.show()
                    2 -> fab.hide()
                }
            }
        })

        // This code is for the newChat button (to test in the beginning):
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    // Change the camera tab (Make it smaller)
    fun resizeTabs() {

        val layout = (tabs.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 0.4f
        layout.layoutParams = layoutParams
    }

    fun onNewChat(v: View) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Contacts permission")
                    .setMessage("This app requires access to your contacts to initiate a conversation")
                    .setPositiveButton("Ask me") { dialog, which -> requestContactPermission() }
                    .setNegativeButton("No") { dialog, which -> } //Don't do anything
                    .show()
            } else {
                requestContactPermission()
            }
        } else {
            // If Permission is Granted then starts newActivity
            startNewActivity()
        }

    }

    // Create a kind of a pop up that request the permission
    fun requestContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            // If I have the permission, I'm going to the fun startNewActivity()
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNewActivity()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_NEW_CHAT -> {
                    val name = data?.getStringExtra(PARAM_NAME) ?: ""
                    val phone = data?.getStringExtra(PARAM_PHONE) ?: ""
                    checkNewChatUser(name, phone)
                }
            }
        }
    }

    fun checkNewChatUser(name: String, phone: String) {

        if (!name.isNullOrEmpty() && !phone.isNullOrEmpty()) {
            firebaseDB.collection(DATA_USERS)
                .whereEqualTo(DATA_USER_PHONE, phone)
                .get()
                .addOnSuccessListener { result ->
                    if (result.documents.size > 0) {
                        chatsFragment.newChat(result.documents[0].id)
                    } else {// If I don't have the user in the DB:

                        AlertDialog.Builder(this)
                            .setTitle("User not found")
                            .setMessage("$name does not have an account. Send them an SMS to install this app.")
                            .setPositiveButton("OK") { dialog, which ->
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("sms:$phone")
                                intent.putExtra(
                                    "sms_body",
                                    "Hi $name! You should install this cool app, so we can chat there."
                                )
                                startActivity(intent)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Please try later, an error ocurred", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
        }
    }


    // After I have the permission to access the user contact
    private fun startNewActivity() {
        startActivityForResult(ContactsActivity.newIntent(this), REQUEST_NEW_CHAT)
    }


    override fun onResume() {
        super.onResume()

        if (firebaseAuth.currentUser == null) {
            startActivity(LoginActivity.newIntent(this))
            finish()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_logOut -> onSignOut()
            R.id.action_profile -> onProfile()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onProfile() {
        startActivity(ProfileActivity.newIntent(this))
    }

    private fun onSignOut() {
        firebaseAuth.signOut()
        startActivity(LoginActivity.newIntent(this))
    }

    inner class SectionPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> statusUpdateFragment
                1 -> chatsFragment
                else -> statusFragment
            }
        }

        override fun getCount(): Int {
            return 3
        }

    }

    companion object {

        val PARAM_NAME = "param name"
        val PARAM_PHONE = "param phone"

        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}


//   This code allows me to see the different views of the fragment (wrote in the beginning):

//    inner class SectionPagerAdapter(fm: FragmentManager) :
//        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//        override fun getItem(position: Int): Fragment {
//            return PlaceHolderFragment.newIntent(position + 1)
//        }
//
//        override fun getCount(): Int {
//            return 3
//        }
//
//    }
//
//    class PlaceHolderFragment : Fragment() {
//
//        override fun onCreateView(
//            inflater: LayoutInflater,
//            container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//            val rootView: View = inflater.inflate(R.layout.fragment_main, container, false)
//            rootView.section_label.text =
//                "Hello Dany from section ${arguments?.getInt(ARG_SECTION_NUMBER)}"
//
//            return rootView
//        }
//
//        companion object {
//            private val ARG_SECTION_NUMBER = "Section number"
//
//            fun newIntent(sectionNumber: Int): PlaceHolderFragment {
//                val fragment = PlaceHolderFragment()
//                val args = Bundle()
//
//                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
//                fragment.arguments = args
//                return fragment
//
//            }
//        }
//    }


