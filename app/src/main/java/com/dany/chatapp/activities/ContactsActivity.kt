package com.dany.chatapp.activities

import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import com.dany.chatapp.R
import util.Contact

class ContactsActivity : AppCompatActivity() {

    private val contactList = ArrayList<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        getContacts()
    }

    private fun getContacts() {
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
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ContactsActivity::class.java)
    }
}