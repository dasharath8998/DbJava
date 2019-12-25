package com.dasharath.hatisamaj.ui.account


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.ui.LoginActivity
import com.dasharath.hatisamaj.ui.commonform.CommonFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_account.view.*

class AccountFragment : Fragment() {

    var registerOption: Array<CharSequence>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        registerOption = arrayOf(
            CommonUtils.AS_EMPLOYEE,
            CommonUtils.AS_BUSINESS,
            CommonUtils.OTHER
        )
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners(view)
    }

    private fun listeners(view: View) {
        view.btnAccountLogin.setOnClickListener {
            activity?.startActivity(Intent(activity,LoginActivity::class.java))
            view.btnAccountLogin.visibility = View.GONE
            view.linearAccountLogin.visibility = View.VISIBLE
        }

        view.tvLogout.setOnClickListener {
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
