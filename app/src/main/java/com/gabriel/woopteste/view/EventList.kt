package com.gabriel.woopteste.view

import android.app.AlertDialog
import android.content.Intent
import android.os.*
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.gabriel.woopteste.R
import com.gabriel.woopteste.http.EventRequest
import com.gabriel.woopteste.model.Event
import kotlin.concurrent.thread

class EventList: AppCompatActivity() {

    private var context : AppCompatActivity? = null

    private var pbEvent : ProgressBar? = null
    private var rvNothingHere : RelativeLayout? = null
    private var lvEventList : ListView? = null

    private var eventList : List<Event> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_event_list)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        context = this

        lvEventList   = findViewById(R.id.lv_event_list)
        pbEvent       = findViewById(R.id.pb_event)
        rvNothingHere = findViewById(R.id.rl_nothing_here)
    }

    override fun onPostCreate(savedInstanceState : Bundle?) {
        super.onPostCreate(savedInstanceState);
        LoadScreen()
    }

    private fun LoadScreen() {
        thread {
            rvNothingHere!!.visibility = View.GONE
            LoadEventList()

            this@EventList.runOnUiThread{
                LoadAdapter()
                pbEvent!!.visibility = View.GONE
            }
        }
    }

    private fun LoadAdapter() {
        try {
            val eventListAdapter = EventListAdapter(eventList, context!!)
            lvEventList!!.adapter = eventListAdapter
            lvEventList!!.dividerHeight = 10
            lvEventList!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val event : Event = adapterView.getItemAtPosition(i) as Event
                GoToEvent(event)
            }
        }
        catch (ex : Exception) {
            ex.printStackTrace()
            ShowError("Ocorreu uma falha ao montar a listagem de eventos")
        }
    }

    private fun LoadEventList() {
        try {
            val reqEvent = EventRequest()
            eventList = reqEvent.getEventList()
        }
        catch (ex : Exception) {
            ex.printStackTrace()
            ShowError(ex.message.toString())
        }
    }

    private fun GoToEvent(event : Event) {
        val intent = Intent(context, EventDetail::class.java)
        intent.putExtra("EVENT_ID", event.id)
        startActivity(intent)
    }

    private fun ShowError(message : String) {
        val newMessage = "$message \r\nTente novamente mais tarde."

        this@EventList.runOnUiThread {
            rvNothingHere!!.visibility = View.VISIBLE

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Atenção")
            alertDialogBuilder.setIcon(R.drawable.ic_alert)
            alertDialogBuilder.setMessage(newMessage)
            alertDialogBuilder.show()
        }
    }

}