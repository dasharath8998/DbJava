package com.dasharath.hatisamaj.ui.account


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.model.UserData
import com.dasharath.hatisamaj.ui.LoginActivity
import com.dasharath.hatisamaj.ui.commonform.CommonFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.SharedPrefUtils
import com.dasharath.hatisamaj.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_profile_settings.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_app.view.*


class AccountFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var userData: DocumentReference? = null

    private var data: UserData? = null
    private var registerOption: Array<CharSequence>? = null
    private var isLoggedIn = false

    val msg = "You have 3 pending request please wait until one of your request got pass"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerOption = arrayOf(
            CommonUtils.AS_EMPLOYEE,
            CommonUtils.AS_BUSINESS,
            CommonUtils.AS_SOCIAL_WORKER,
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
        currentUser = mAuth?.currentUser
        view.toolbar.tvTitle.text = "Account & Settings"
        if (currentUser != null) {
            if(!currentUser?.isEmailVerified!!){
                mAuth?.signOut()
                return
            }
            view.aviLoadingAccount.show()
            isLoggedIn = true

            if (currentUser != null) {
                setData(view)
            }
            view.linearSignIn.visibility = View.GONE
            view.linearAccountLogin.visibility = View.VISIBLE
        } else {
            isLoggedIn = false
        }
    }

    private fun setData(view: View) {
        userData = db?.collection(CommonUtils.USER)?.document(currentUser?.uid!!)
        userData?.get()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                view.aviLoadingAccount.hide()
                data = task.result?.toObject(UserData::class.java)
                view.tvAccountName.text = data?.name
                view.tvAccountEmail.text = data?.email
                Log.d("TAG", "Object: $data")
            } else {
                view.aviLoadingAccount.hide()
                Log.d("TAG", "Cached get failed: ", task.exception)
            }
        }
    }

    private fun listeners(view: View) {

        view.btnAccountLogin.setOnClickListener {
            activity?.startActivity(Intent(activity, LoginActivity::class.java))
        }

        view.tvLogout.setOnClickListener {
            mAuth?.signOut()
            SharedPrefUtils().storeUserType(context!!, "")
            view.linearSignIn.visibility = View.VISIBLE
            view.linearAccountLogin.visibility = View.GONE
        }

//        view.tvRegisterStudent.setOnClickListener {
//            val intent = Intent(context, CommonFormActivity::class.java)
//            activity?.startActivity(intent.putExtra(CommonUtils.TITLE, CommonUtils.AS_STUDENT))
//        }

        view.tvRegister.setOnClickListener {
            view.aviLoadingAccount.show()
            db?.collection(CommonUtils.PEOPLE)
                ?.whereEqualTo(CommonUtils.STATUS, CommonUtils.PENDING)
                ?.whereEqualTo(CommonUtils.UID, currentUser?.uid.toString())?.get()
                ?.addOnSuccessListener {
                    view.aviLoadingAccount.hide()
                    val peopleId = it.documents
                    if (peopleId.size > 2) {
                        Utils.showAlert(context!!, msg)
                    } else {
                        startNewActivity()
                    }
                }
                ?.addOnFailureListener {
                    view.aviLoadingAccount.hide()
                    aviLoading.hide()
                    Log.d("Failed", "Failed SearchFragment : ${it.toString()}")
                }

        }

        view.ivSettings.setOnClickListener {
            if (data != null)
                showUpdateDialog(view)

        }
    }

    private fun startNewActivity() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Select Registration Type")
        builder.setItems(registerOption) { dialog, which ->
            val intent = Intent(context, CommonFormActivity::class.java)
            when (which) {
                0 -> intent.putExtra(CommonUtils.TITLE, CommonUtils.AS_EMPLOYEE)
                1 -> intent.putExtra(CommonUtils.TITLE, CommonUtils.AS_BUSINESS)
                2 -> intent.putExtra(CommonUtils.TITLE, CommonUtils.AS_SOCIAL_WORKER)
                3 -> intent.putExtra(CommonUtils.TITLE, CommonUtils.OTHER)
            }
            activity?.startActivity(intent)
        }
        builder.show()
    }

    private fun showUpdateDialog(view: View) {
        val dialog = Dialog(context!!, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setContentView(R.layout.dialog_profile_settings)
        dialog.etDialogName.setText(data?.name)
        dialog.etDialogEmail.setText(data?.email)

        dialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnUpdate.setOnClickListener {
            dialog.aviLoadingAccount.show()
            db?.collection(CommonUtils.USER)?.document(currentUser?.uid!!)
                ?.update(
                    CommonUtils.NAME,
                    dialog.etDialogName.text.toString(),
                    CommonUtils.EMAIL,
                    dialog.etDialogEmail.text.toString()
                )
                ?.addOnSuccessListener {
                    context?.toast("Profile updated!")
                    dialog.aviLoadingAccount.hide()
                    setData(view)
                    dialog.dismiss()
                }
                ?.addOnFailureListener {
                    context?.toast("Please try after some time!")
                    dialog.dismiss()
                }
        }

        dialog.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
