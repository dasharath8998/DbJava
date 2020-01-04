package com.dasharath.hatisamaj.ui.personinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.model.PersonDetailModel
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

    private fun init() {
        val personData = intent.getSerializableExtra(CommonUtils.PERSONAL)!! as PersonDetailModel
        Glide.with(this@PersonInfoActivity).load(personData.image_url).placeholder(R.drawable.ic_person_male).into(ivPersonInfo)
        tvPersonName.text = personData.name
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,personData)
        viewPager?.adapter = viewPagerAdapter
        tabs.setupWithViewPager(viewPager)
    }

    private fun listeners() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}
