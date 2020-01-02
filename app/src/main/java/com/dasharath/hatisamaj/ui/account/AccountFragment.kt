package com.dasharath.hatisamaj.ui.account


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.model.UserData
import com.dasharath.hatisamaj.ui.LoginActivity
import com.dasharath.hatisamaj.ui.commonform.CommonFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.fragment_account.view.*

class AccountFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var userData: DocumentReference? = null

    private var data: UserData? = null
    private var registerOption: Array<CharSequence>? = null
    private var isLoggedIn = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        registerOption = arrayOf(
            CommonUtils.AS_EMPLOYEE,
            CommonUtils.AS_BUSINESS,
            CommonUtils.OTHER
        )
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        listeners(view)
    }

    private fun init(view: View) {
        view.aviLoadingAccount.show()
        currentUser = mAuth?.currentUser
        if (currentUser != null) {
            isLoggedIn = true

            userData = db?.collection(CommonUtils.USER)?.document(currentUser?.uid!!)
            val source = Source.CACHE
            userData?.get(source)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    view.aviLoadingAccount.hide()
                    data = task.result?.toObject(UserData::class.java)
                    view.tvAccountName.text = data?.name
                    Log.d("TAG", "Object: $data")
                } else {
                    view.aviLoadingAccount.hide()
                    Log.d("TAG", "Cached get failed: ", task.exception)
                }
            }

            view.btnAccountLogin.visibility = View.GONE
            view.linearAccountLogin.visibility = View.VISIBLE
        } else {
            isLoggedIn = false
        }
    }

    private fun listeners(view: View) {
        view.btnAccountLogin.setOnClickListener {
            activity?.startActivity(Intent(activity,LoginActivity::class.java))
        }

        view.tvLogout.setOnClickListener {
            mAuth?.signOut()
            view.btnAccountLogin.visibility = View.VISIBLE
            view.linearAccountLogin.visibility = View.GONE
        }

        view.tvRegisterStudent.setOnClickListener {
            val intent = Intent(context, CommonFormActivity::class.java)
            activity?.startActivity(intent.putExtra(CommonUtils.TITLE, CommonUtils.AS_STUDENT))
        }

        view.tvRegister.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Select Registration Type")
            builder.setItems(registerOption) { dialog, which ->
                val intent = Intent(context, CommonFormActivity::class.java)
                when (which) {
                    0 -> intent.putExtra(CommonUtils.TITLE, CommonUtils.AS_EMPLOYEE)
                    1 -> intent.putExtra(CommonUtils.TITLE, CommonUtils.AS_BUSINESS)
                    2 -> intent.putExtra(CommonUtils.TITLE, CommonUtils.OTHER)
                }
                activity?.startActivity(intent)
            }
            builder.show()
        }
    }
}
