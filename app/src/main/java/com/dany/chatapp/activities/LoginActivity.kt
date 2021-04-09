package com.dany.chatapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.dany.chatapp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    // When the user authenticated, do this:
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid // The id of the user of the firebase backend

        if (user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)

        setTextChangeListener(emailET, emailTIL)
        setTextChangeListener(passwordET, passwordTIL)

        // While the progress layout I don't want the user are able to click anything:
        progressLayout.setOnTouchListener { v, event -> true }

    }

    private fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }

        })
    }

    fun onLogin(v: View) {

        var proceed = true

        if (emailET.text.isNullOrEmpty()) {
            emailTIL.error = "Email is required"
            emailTIL.isErrorEnabled = true
            proceed = false
        }
        if (passwordET.text.isNullOrEmpty()) {
            passwordTIL.error = "The password is required"
            passwordTIL.isErrorEnabled = true
            proceed = false
        }

        if (proceed) {
            progressLayout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
                emailET.text.toString().trim(),
                passwordET.text.toString().trim()
            )
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        progressLayout.visibility = View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            "${task.exception?.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("===>Unsuccessful login", "${task.exception?.localizedMessage}")
                        Log.w(
                            "Log.w===>Unsuccessful",
                            "signInWithCustomToken:failure",
                            task.exception
                        )
                    } else {
                        Toast.makeText(this@LoginActivity, "Successful login", Toast.LENGTH_LONG)
                            .show()
                        Log.d("===>Successful login", "${task.exception?.localizedMessage}")
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
        Toast.makeText(this, "On Start function", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
        Toast.makeText(this, "On Stop function", Toast.LENGTH_SHORT).show()

    }

    fun onSignup(v: View) {
        startActivity(SignUpActivity.newIntent(this))
        Toast.makeText(this, "from Login to SignUp", Toast.LENGTH_SHORT).show()
        finish()

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

}