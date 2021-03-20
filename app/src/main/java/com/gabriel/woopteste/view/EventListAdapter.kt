package com.gabriel.woopteste.view

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gabriel.woopteste.R
import com.gabriel.woopteste.common.Utils
import com.gabriel.woopteste.model.Event
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class EventListAdapter( items : List<Event>, ctx : Context)
    : ArrayAdapter<Event> (ctx, R.layout.act_event_list_item, items) {

    private class EventViewHolder {
        internal var image: ImageView? = null
        internal var title: TextView? = null
        internal var date: TextView? = null
        internal var price: TextView? = null
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view

        val viewHolder: EventViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.act_event_list_item, viewGroup, false)

            viewHolder = EventViewHolder()
            viewHolder.image = view!!.findViewById<View>(R.id.iv_image) as ImageView
            viewHolder.title = view!!.findViewById<View>(R.id.tv_title) as TextView
            viewHolder.date = view!!.findViewById<View>(R.id.tv_date) as TextView
            viewHolder.price = view!!.findViewById<View>(R.id.tv_price) as TextView

        } else {
            viewHolder = view.tag as EventViewHolder
        }

        val event = getItem(i)
        loadImage(viewHolder.image, event!!.image)
        viewHolder.title!!.text = event!!.title
        viewHolder.date!!.text = Utils.formatDate(event!!.date, Utils.DATE_FORMAT_PT_BR)
        viewHolder.price!!.text = Utils.formatMoney(event!!.price)

        view.tag = viewHolder

        return view
    }

    private fun loadImage(imageView: ImageView?, url : String?) {
        if(imageView == null || url.isNullOrEmpty())
            return

        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    imageView.setAlpha(0f);
                    imageView.animate().setDuration(200).alpha(1f).start();
                }

                override fun onError(e: Exception?) {
                }

            })
    }
}