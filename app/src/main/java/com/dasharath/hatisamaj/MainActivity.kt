package com.dasharath.hatisamaj

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dasharath.hatisamaj.ui.account.AccountFragment
import com.dasharath.hatisamaj.ui.home.HomeFragment
import com.dasharath.hatisamaj.ui.notification.NotificationFragment
import com.dasharath.hatisamaj.ui.search.SearchFragment
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener{

    private var doubleBackToExitPressedOnce = false
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        navigation()
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun init() {
        bottom_bar.enableAnimation(false)
        replaceFragment(HomeFragment())
    }

    private fun navigation() {
        bottom_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragmentTab -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.searchFragmentTab -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.notificationsFragmentTab -> {
                    replaceFragment(NotificationFragment())
                    true
                }
                R.id.accountFragmentTab -> {
                    replaceFragment(AccountFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameMain, fragment).commit()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(findViewById(R.id.frameMain), "You are offline", Snackbar.LENGTH_LONG)
            val params = snackBar!!.view.layoutParams as FrameLayout.LayoutParams
            params.setMargins(10,0,10,145)
            snackBar!!.view.layoutParams = params
            snackBar!!.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }
}
