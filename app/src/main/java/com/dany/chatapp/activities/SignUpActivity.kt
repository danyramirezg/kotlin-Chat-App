package com.dany.chatapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dany.chatapp.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun onClick(v: View) {
        startActivity(MainActivity.newIntent(this))
        Toast.makeText(this, "from SignUp to Main", Toast.LENGTH_SHORT).show()
        finish()
    }
    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}