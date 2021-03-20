package com.gabriel.woopteste.common

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {
        val DATE_FORMAT_PT_BR : String = "dd/MM/yyyy"

        fun formatMoney(value : Double?) : String {
            if (value == null)
                return ""

            val currency = NumberFormat.getCurrencyInstance();
            return currency.format(value)
        }

        fun formatDate(timestamp : Long?, format : String) : String {
            if (timestamp == null)
                return ""

            val dateFormat = SimpleDateFormat(format)
            val date = Date(timestamp)
            return dateFormat.format(date)
        }
    }

}