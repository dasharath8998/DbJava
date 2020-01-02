package com.dasharath.hatisamaj.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.dasharath.hatisamaj.MainActivity
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var userData: DocumentReference? = null
    private var currentUserId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        listeners()
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun listeners() {
        btnLoginButton.setOnClickListener {
            if (formValid()) {
                aviLoadingLogin.show()
                allowUserToLogin()
            } else {
                toast("Please fill require fields")
            }
        }

        tvNeedNewAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

    }

    private fun allowUserToLogin() {
        mAuth?.signInWithEmailAndPassword(etLoginEmail.text.toString(), etLoginPassword.text.toString())?.addOnCompleteListener {
            if (it.isSuccessful) {

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }

                        currentUserId = mAuth?.currentUser?.uid!!
                        userData = db?.collection(CommonUtils.USER)?.document(currentUserId!!)
                        // Get new Instance ID token
                        val token = task.result!!.token
                        userData?.update(CommonUtils.DEVICE_TOKEN, token)
                            ?.addOnSuccessListener {
                                toast("Login successful")
                            }
                        Log.d("TAG", token)
                        aviLoadingLogin.hide()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        finish()
                    })


            } else {
                aviLoadingLogin.hide()
                val message = it.exception.toString()
                toast("Error: $message")
            }

        }
    }

    private fun formValid(): Boolean {
        if (!etLoginEmail.checkTextValue(isEmail = true)) return false
        if (!etLoginPassword.checkTextValue(checkForPass = true)) return false
        return true
    }

    private fun EditText.checkTextValue(
        checkForPass: Boolean = false,
        isEmail: Boolean = false
    ): Boolean {

        val value = text.toString()
        if (value == "") {
            this.error = "This field can't be blank"
            return false
        }

        if (isEmail) {
            if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                this.error = "Enter valid email"
                return false
            }
        }

        if (checkForPass) {
            if (value.length < 6) {
                error = "Password length must be 6 Characters"
                return false
            }
        }

        return true
    }
}
