package com.dasharath.hatisamaj.ui.personinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_person_info.*


class PersonInfoActivity : AppCompatActivity() {

    var viewPagerAdapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_info)
        init()
        listeners()
    }

    private fun listeners() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun init() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,intent.getStringExtra(CommonUtils.CATEGORY)!!)
        viewPager?.adapter = viewPagerAdapter
        tabs.setupWithViewPager(viewPager)
    }
}
