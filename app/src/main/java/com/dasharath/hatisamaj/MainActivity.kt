package com.dasharath.hatisamaj

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dasharath.hatisamaj.ui.account.AccountFragment
import com.dasharath.hatisamaj.ui.home.HomeFragment
import com.dasharath.hatisamaj.ui.notification.NotificationFragment
import com.dasharath.hatisamaj.ui.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        navigation()
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
}
