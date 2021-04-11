package listeners

import android.provider.ContactsContract

interface ContactsClickListener{
    fun onContactClicked(name: String?, phone: String?)
}