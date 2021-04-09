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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.emailET
import kotlinx.android.synthetic.main.activity_sign_up.emailTIL
import kotlinx.android.synthetic.main.activity_sign_up.passwordET
import kotlinx.android.synthetic.main.activity_sign_up.passwordTIL
import kotlinx.android.synthetic.main.activity_sign_up.progressLayout
import util.DATA_USERS
import util.User

class SignUpActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid

        if (user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_sign_up)

        setTextChangeListener(nameET, nameTIL)
        setTextChangeListener(phoneET, phoneTIL)
        setTextChangeListener(emailET, emailTIL)
        setTextChangeListener(passwordET, passwordTIL)

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

    fun onSignUp(v: View) {
        var proceed = true

        if (nameET.text.isNullOrEmpty()) {
            nameTIL.error = "Name is required"
            nameTIL.isErrorEnabled = true
            proceed = false
        }

        if (phoneET.text.isNullOrEmpty()) {
            phoneTIL.error = "Phone number is required"
            phoneTIL.isErrorEnabled = true
            proceed = false
        }

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
            firebaseAuth.createUserWithEmailAndPassword(
                emailET.text.toString().trim(),
                passwordET.text.toString().trim()
            )
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        progressLayout.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Sign up error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()

                    } else if (firebaseAuth.uid != null) {
                        // Database:
                        val name = nameET.text.toString().trim()
                        val phone = phoneET.text.toString().trim()
                        val email = emailET.text.toString().trim()

                        val user = User(name, phone, email, "", "Hi! It's me!", "", "")
                        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)
                    }
                    progressLayout.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    progressLayout.visibility = View.GONE
                    e.printStackTrace()
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
        Toast.makeText(this, "On Stop function", Toast.LENGTH_SHORT).show()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)

    }

//    fun onSignUp(v: View) {
//        startActivity(MainActivity.newIntent(this))
//        Toast.makeText(this, "from SignUp to Main", Toast.LENGTH_SHORT).show()
//        finish()
//    }

    fun onLogin(v: View) {
        startActivity(LoginActivity.newIntent(this))
        Toast.makeText(this, "from SignUp to Login", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}