package com.dasharath.hatisamaj.ui

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dasharath.hatisamaj.MainActivity
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.dasharath.hatisamaj.utils.SharedPrefUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SplashActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
        checkAppStatus()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun init() {
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun checkAppStatus() {
        db?.collection(CommonUtils.UTILS)?.get()?.addOnSuccessListener {

            val appUtils = it.documents[0]

            val dbAppVersion = appUtils[CommonUtils.APP_VERSION].toString()
            val isAppRunning = appUtils[CommonUtils.APP_RUNNIG] as Boolean
            val isForceUpdate = appUtils[CommonUtils.IS_FORCED] as Boolean
            val updateMessage = appUtils[CommonUtils.UPDATE_MESSAGE].toString()

            val pInfo: PackageInfo =
                this@SplashActivity.packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName

            if (!isAppRunning) {
                val intent = Intent(this@SplashActivity, UnderConstructionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                if (version == dbAppVersion) {
                    navigateToHome()
                } else {
                    showUpdateDialog(updateMessage,isForceUpdate)
                }
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(
                findViewById(R.id.mainCommon),
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    private fun showUpdateDialog(updateMessage: String, isForce: Boolean = false) {
        val dialog = AlertDialog.Builder(this@SplashActivity)
        dialog.setTitle("Hati samaj")
        dialog.setMessage(updateMessage)
        dialog.setPositiveButton("Update") { dialog, which ->
            if (!isForce)
                navigateToHome()
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
        if (!isForce) {
            dialog.setNegativeButton(R.string.no) { dialog, which ->
                dialog.dismiss()
                navigateToHome()
            }
        }
        dialog.show()
    }
}
