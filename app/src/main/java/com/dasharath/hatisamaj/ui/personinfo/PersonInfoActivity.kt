package com.dasharath.hatisamaj.ui.personinfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.dasharath.hatisamaj.utils.ViewPagerAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_person_info.*


class PersonInfoActivity : AppCompatActivity(),ConnectivityReceiver.ConnectivityReceiverListener {

    var viewPagerAdapter: ViewPagerAdapter? = null
    var firstTimeStatus: String = ""
    var backTimeStatus: String = ""
    var personData: PersonDetailModel? = null
    private var snackBar: Snackbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        init()
        listeners()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun init() {
        personData = intent.getSerializableExtra(CommonUtils.PERSONAL)!! as PersonDetailModel
        Glide.with(this@PersonInfoActivity).load(personData!!.image_url).placeholder(R.drawable.ic_person_male).into(ivPersonInfo)
        tvPersonName.text = personData!!.name
        firstTimeStatus = personData!!.status
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,personData!!)
        viewPager?.adapter = viewPagerAdapter
        tabs.setupWithViewPager(viewPager)
    }

    private fun listeners() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(
                findViewById(R.id.mainDetail),
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    override fun onBackPressed() {
        backTimeStatus = personData!!.status
        if(firstTimeStatus != backTimeStatus) setResult(Activity.RESULT_OK, Intent())
        super.onBackPressed()
    }
}
