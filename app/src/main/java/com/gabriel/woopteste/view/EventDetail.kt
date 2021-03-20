package com.gabriel.woopteste.view

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.gabriel.woopteste.R
import com.gabriel.woopteste.common.Utils
import com.gabriel.woopteste.http.EventRequest
import com.gabriel.woopteste.model.Event
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlin.concurrent.thread

class EventDetail : AppCompatActivity() {

    private var context : AppCompatActivity? = null

    private var pbEvent : ProgressBar? = null
    private var ivImage : ImageView? = null
    private var tvTitle : TextView? = null
    private var tvDate : TextView? = null
    private var tvPrice : TextView? = null
    private var tvDescription : TextView? = null
    private var llPanelDatePrice : LinearLayout? = null
    private var btCheckin : MaterialButton? = null

    private var eventId : Int? = null
    private var event : Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_event_detail)

        context = this

        eventId = intent.getIntExtra("EVENT_ID", 0)
        ConfigureToolbar()
    }

    override fun onPostCreate(savedInstanceState : Bundle?) {
        super.onPostCreate(savedInstanceState);
        LoadScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.event_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        try {
            if (item.itemId == android.R.id.home)
                finish()

            if (event == null)
                throw Exception("Ainda não preenchemos todos dados do evento")

            when(item.itemId) {
                R.id.mi_share -> { ShareEvent() }
                R.id.mi_maps -> { OpenMap() }
            }
        }
        catch (ex : Exception) {
            ex.message?.let { ShowError(it) }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun ConfigureToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun LoadScreen() {
        thread {
            try {
                LoadEvent()

                this@EventDetail.runOnUiThread{
                    try {
                        LoadFields()
                        SetContentValues()
                        llPanelDatePrice!!.visibility = View.VISIBLE
                        pbEvent!!.visibility = View.GONE
                    }
                    catch (ex : Exception) {
                        ex.printStackTrace()
                        ShowError("Ocorreu um erro ao preencher os dados do evento")
                    }
                }
            }
            catch (ex : Exception) {
                ex.printStackTrace()
                ShowError(ex.message.toString())
            }
        }
    }

    private fun LoadEvent() {
        try {
            val reqEvent = EventRequest()
            event = reqEvent.getEvent(eventId!!)
        }
        catch (ex : Exception) {
            throw ex
        }
    }

    private fun LoadFields() {
        try {
            llPanelDatePrice = findViewById(R.id.ll_panel_date_price)
            pbEvent          = findViewById(R.id.pb_event)
            ivImage          = findViewById(R.id.iv_image)
            tvTitle          = findViewById(R.id.tv_title)
            tvDate           = findViewById(R.id.tv_date)
            tvPrice          = findViewById(R.id.tv_price)
            tvDescription    = findViewById(R.id.tv_description)
            btCheckin        = findViewById(R.id.bt_checkin)
        }
        catch (ex : Exception) {
            throw Exception("Tivemos uma falha ao obter os componentes da tela.")
        }
    }

    private fun SetContentValues() {
        try {
            LoadImage()
            tvTitle!!.text = event!!.title
            tvDate!!.text = Utils.formatDate(event!!.date, Utils.DATE_FORMAT_PT_BR)
            tvPrice!!.text = Utils.formatMoney(event!!.price)
            tvDescription!!.text = event!!.description
            btCheckin!!.setOnClickListener { OpenCheckinDialog() }
        }
        catch (ex : Exception) {
            throw Exception("Falha ao buscar dados do evento")
        }
    }

    private fun OpenMap() {
        val lat = event!!.latitude.toString()
        val long = event!!.longitude.toString()
        val uri  = String.format("geo:%s,%s?q=%s,%s (%s)", lat, long, lat, long, event!!.title)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    private fun ShareEvent() {

        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, buildTextToShare())
        sendIntent.type = "text/plain*"
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun LoadImage() {
        Picasso.get()
            .load(event!!.image)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(ivImage, object : Callback {
                override fun onSuccess() {
                    ivImage!!.setAlpha(0f);
                    ivImage!!.animate().setDuration(500).alpha(1f).start();
                }
                override fun onError(e: java.lang.Exception?) {
                }
            })
    }

    private fun OpenCheckinDialog() {
        try {
            if (event == null)
                throw Exception("Ainda não preenchemos todos dados do evento")

            val checkinDialog = CheckinDialog()
            checkinDialog.context = context
            checkinDialog.event = event
            checkinDialog.show(supportFragmentManager, "CHECKIN-DIALOG")
        }
        catch (ex : Exception) {
            ex.message?.let { ShowError(it) }
        }
    }

    private fun buildTextToShare() : String {
        val format = "Olá,\r\nVocê está sendo convidado para participar do(a) '%s', que irá ocorrer no dia %s.\r\nTambém iremos ter diversas atrações, tudo isso por apenas %s.\r\nVocê não vai ficar fora dessa, vai?"
        val message = String.format(format,
            event!!.title,
            Utils.formatDate(event!!.date, Utils.DATE_FORMAT_PT_BR),
            Utils.formatMoney(event!!.price)
        )
        return message
    }

    private fun ShowError(message : String) {
        val newMessage = "$message \r\nTente novamente mais tarde."

        this@EventDetail.runOnUiThread {

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Atenção")
            alertDialogBuilder.setIcon(R.drawable.ic_alert)
            alertDialogBuilder.setMessage(newMessage)
            alertDialogBuilder.setOnDismissListener {
                finish()
            }
            alertDialogBuilder.show()
        }
    }
}