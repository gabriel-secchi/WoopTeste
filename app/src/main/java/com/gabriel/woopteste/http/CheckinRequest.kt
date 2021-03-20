package com.gabriel.woopteste.http

import com.gabriel.woopteste.model.Checkin
import com.gabriel.woopteste.model.CheckinResponse
import com.gabriel.woopteste.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CheckinRequest {
    private val endPoint : String = "checkin"

    fun sendCheckin(checkin : Checkin) : Boolean {
        try {
            val responseBody = requestWoopApi(checkin)

            if(responseBody.isNullOrEmpty())
                throw Exception("Não foi possível fazer seu checkin check-in.\r\nTente novamente mais tarde.")

            val response = Gson().fromJson(responseBody, CheckinResponse::class.java)

            if (response.code != 200)
                throw Exception("Não foi possível fazer seu checkin check-in.\r\nTente novamente mais tarde.")

            println(response)
            return true
        }
        catch ( ex : Exception ) {
            ex.printStackTrace()
            throw ex
        }
    }

    private fun requestWoopApi(checkin : Checkin?) : String? {
        try {
            val woopApi : WoopApi = WoopApi()
            val sendBody = Gson().toJson(checkin)

            woopApi.POST(endPoint, sendBody)

            if (woopApi.error != null)
                throw Exception("Estamos com problema para fazer seu check-in.\r\nTente novamente mais tarde.")

            return woopApi.responseBody
        }
        catch ( ex : Exception ) {
            ex.printStackTrace()
            throw ex
        }
    }

}