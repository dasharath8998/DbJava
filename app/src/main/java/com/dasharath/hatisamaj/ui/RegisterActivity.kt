package com.dasharath.hatisamaj.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar_app.view.*


class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
        listeners()
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        toolbar.tvTitle.text = "Register"
    }

    private fun listeners() {
        tvAlredyHaveAccountLink.setOnClickListener {
            onBackPressed()
        }

        btnRegisterButton.setOnClickListener {
            if (formIsValid()) {
                aviLoadingRegister.show()
                createNewAccount()
            }
        }
    }

    private fun createNewAccount() {
        mAuth?.createUserWithEmailAndPassword(etRegisterEmail.text.toString(), etRegisterPassword.text.toString())?.addOnCompleteListener {
            if (it.isSuccessful) {
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }

                        val token = task.result?.token

                        val userDetail = hashMapOf(
                            CommonUtils.EMAIL to etRegisterEmail.text.toString(),
                            CommonUtils.PASS to etRegisterPassword.text.toString(),
                            CommonUtils.NAME to etRegisterName.text.toString(),
                            CommonUtils.DEVICE_TOKEN to token,
                            CommonUtils.USERTYPE to "user"
                        )

                        db!!.collection(CommonUtils.USER)
                            .document(mAuth?.currentUser?.uid!!.toString()).set(userDetail)
                            .addOnSuccessListener { documentReference ->
                                aviLoadingRegister.hide()
                                sendVerificationLink()
                                Log.d("TAG", "DocumentSnapshot added with $documentReference")
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error adding document", e)
                            }
                        toast("Account created successfully")
                    })
            } else {
                val message = it.exception.toString()
                toast("Error: $message")
            }
        }
    }

    fun sendVerificationLink() {
        mAuth?.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                AlertDialog.Builder(this@RegisterActivity)
                    .setTitle("Hati kshtriya samaj")
                    .setMessage("Email verification link send to your mail please verify than login")
                    .setPositiveButton("Ok") { dialog, which ->
                        startActivity(
                            Intent(
                                this@RegisterActivity,
                                LoginActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }
                    .show()
            } else {
                toast("Something went wrong please try again later")
            }
        }
    }

    private fun formIsValid(): Boolean {
        if (!etRegisterEmail.checkTextValue(isEmail = true)) return false
        if (!etRegisterName.checkTextValue()) return false
        if (!etRegisterPassword.checkTextValue(checkForPass = true)) return false
        if (!etConfirmPassword.checkTextValue(checkForPass = true)) return false
        if (etRegisterPassword.text.toString() != etConfirmPassword.text.toString()) {
            toast("Password and confirm password must be same")
            return false
        }
        return true
    }

    private fun EditText.checkTextValue(
        checkForPass: Boolean = false,
        isEmail: Boolean = false
    ): Boolean {

        val value = this.text.toString()
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
