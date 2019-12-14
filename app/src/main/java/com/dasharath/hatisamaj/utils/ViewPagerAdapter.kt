package com.dasharath.hatisamaj.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dasharath.hatisamaj.ui.personinfotab.OtherInfo
import com.dasharath.hatisamaj.ui.personinfotab.PersonalInfo


class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,  BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = PersonalInfo()
        } else if (position == 1) {
            fragment = OtherInfo()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "Personal"
        } else if (position == 1) {
            title = "Other"
        }
        return title
    }
}