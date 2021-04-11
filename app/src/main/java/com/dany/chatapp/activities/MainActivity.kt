package com.dany.chatapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.Placeholder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dany.chatapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import fragments.ChatsFragment
import fragments.StatusFragment
import fragments.StatusUpdateFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : AppCompatActivity() {

    private var mySectionPagerAdapter: SectionPagerAdapter? = null

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val statusUpdateFragment = StatusUpdateFragment()
    private val chatsFragment = ChatsFragment()
    private val statusFragment = StatusFragment()


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

//   This code is for the newChat button (to test in the beginning):
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

    inner class SectionPagerAdapter(fm: FragmentManager):
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getItem(position: Int): Fragment {
            return when(position){
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


