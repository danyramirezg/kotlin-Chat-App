package com.dany.chatapp.activities

import adapters.ContactsAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dany.chatapp.R
import kotlinx.android.synthetic.main.activity_contact.*
import listeners.ContactsClickListener
import util.Contact

class ContactsActivity : AppCompatActivity(), ContactsClickListener {

    private val contactList = ArrayList<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        getContacts()
    }

    private fun getContacts() {
        progressLayout.visibility = View.VISIBLE
        contactList.clear()

        val newList = ArrayList<Contact>()

        // Android way to retrieve contact information:
        val phones: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone
                .CONTENT_URI, null, null, null, null
        )

        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            newList.add(Contact(name, phoneNumber))
        }
        contactList.addAll(newList)
        phones.close()

        setupList()
    }

    fun setupList(){
        progressLayout.visibility = View.GONE
        val contactAdapter = ContactsAdapter(contactList)
        contactAdapter.setOnItemClickListener(this)
        contactsRV.apply{

            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = contactAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onContactClicked(name: String?, phone: String?) {
        val intent = Intent()
        intent.putExtra(MainActivity.PARAM_NAME, name)
        intent.putExtra(MainActivity.PARAM_PHONE, phone)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ContactsActivity::class.java)
    }
}