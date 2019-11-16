package com.dasharath.hatisamaj.ui.notification


import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemNotificationBinding
import com.dasharath.hatisamaj.databinding.ItemPersonInfoBinding
import com.dasharath.hatisamaj.model.NotificationModel
import com.dasharath.hatisamaj.model.PersonModel
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_notification.view.*

class NotificationFragment : Fragment() {

    var notificationList: ArrayList<NotificationModel>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationList = ArrayList()
        for (i in 0..20){
            if(i % 2 == 0) {
                notificationList!!.add(NotificationModel("", "", "Name Male $i", "This is description for male version and also test for multiline text so test test teest test test", "male"))
            } else {
                notificationList!!.add(NotificationModel("", "", "Name Female $i", "Female testing", "female"))
            }
        }
        if(this@NotificationFragment.isVisible) {
            setAdapter(view)
        }
    }

    private fun setAdapter(view: View) {
        view.rvNotification.layoutManager = LinearLayoutManager(context)
        LastAdapter(notificationList!!, BR.notificationData).map<NotificationModel,ItemNotificationBinding>(R.layout.item_notification){
            onBind {
                if(notificationList!![it.adapterPosition].gender == "male") {
                    Glide.with(context!!).load(notificationList!![it.adapterPosition].imagePerson)
                        .placeholder(R.drawable.ic_person_male).into(it.binding.ivItemNotificationPerson)
                } else {
                    Glide.with(context!!).load(notificationList!![it.adapterPosition].imagePerson)
                        .placeholder(R.drawable.ic_person_female).into(it.binding.ivItemNotificationPerson)
                }

                val name = "<b>${notificationList!![it.adapterPosition].name}: </b> ${notificationList!![it.adapterPosition].description}"
                it.binding.tvNotificationDescription.text = HtmlCompat.fromHtml(name,HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }.into(view.rvNotification)
    }

}
