package com.gabriel.woopteste.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import com.gabriel.woopteste.R
import com.gabriel.woopteste.http.CheckinRequest
import com.gabriel.woopteste.model.Checkin
import com.gabriel.woopteste.model.Event
import kotlin.concurrent.thread

class CheckinDialog : AppCompatDialogFragment() {
    var context : AppCompatActivity? = null
    var event : Event? = null

    private var etName : EditText? = null
    private var etMail : EditText? = null
    private var btCancelar : Button? = null
    private var btCheckin : Button? = null
    private var viewInflate : View? = null
    private var pbCheckin : ProgressBar? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        viewInflate = inflater.inflate(R.layout.act_popup_checkin, null)

        builder.setTitle("Check-in")
        builder.setView(viewInflate)

        LoadFields()
        SetContentValues()

        return builder.create()
    }

    private fun LoadFields() {
        etName = viewInflate!!.findViewById(R.id.et_name)
        etMail = viewInflate!!.findViewById(R.id.et_mail)
        btCancelar = viewInflate!!.findViewById(R.id.bt_cancelar)
        btCheckin = viewInflate!!.findViewById(R.id.bt_checkin)
        pbCheckin = viewInflate!!.findViewById(R.id.pb_checkin)
    }

    private fun SetContentValues() {
        btCancelar!!.setOnClickListener { dismiss() }
        btCheckin!!.setOnClickListener {
            try{
                ValidateFields()
                MakeCheckin()
            }
            catch (ex : Exception) {
                ex.printStackTrace()
                ex.message?.let { ShowMessage(it, true) }
            }
        }
    }

    private fun ValidateFields() {
        if (etName!!.text.isNullOrEmpty() || etMail!!.text.isNullOrEmpty())
            throw Exception("Os campo Nome e E-mail são obrigatórios")
    }

    private fun MakeCheckin() {
        thread {
            try {
                controleProgressBar(true)
                val checkin = Checkin(event!!.id, etName!!.text.toString(), etMail!!.text.toString())
                val checkinRequest = CheckinRequest()
                checkinRequest.sendCheckin(checkin)

                ShowMessage("Seu checkin foi efetuado com sucesso!", false)

            }
            catch (ex : Exception) {
                ex.printStackTrace()
                ex.message?.let { ShowMessage(it, true) }
            }
            finally {
                controleProgressBar(false)
            }
        }
    }

    private fun controleProgressBar(toShow : Boolean ) {

        context!!.runOnUiThread(){
            if(toShow) {
                pbCheckin!!.visibility = View.VISIBLE
            }
            else
                pbCheckin!!.visibility = View.GONE
        }
    }

    private fun ShowMessage(message : String, warning : Boolean) {
        context!!.runOnUiThread() {
            val alertDialogBuilder = AlertDialog.Builder(context)
            if(warning)
                alertDialogBuilder.setTitle("Atenção")
            alertDialogBuilder.setMessage(message)

            if(!warning) {
                alertDialogBuilder.setOnDismissListener {
                    dismiss()
                }
            }

            alertDialogBuilder.show()
        }
    }
}