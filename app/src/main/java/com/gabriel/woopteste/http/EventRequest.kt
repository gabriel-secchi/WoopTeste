package com.gabriel.woopteste.http

import com.gabriel.woopteste.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EventRequest {
    private val endPoint : String = "events"

    fun getEventList() : List<Event> {
        try {
            val responseBody = requestWoopApi(null)

            if(responseBody.isNullOrEmpty())
                throw Exception("Não encontramos nenhum evento disponível")

            val listEventType = object : TypeToken<List<Event>>() { }.type
            val eventList = Gson().fromJson<List<Event>>(responseBody, listEventType)
            return eventList
        }
        catch ( ex : Exception ) {
            ex.printStackTrace()
            throw ex
        }
    }

    fun getEvent(eventId : Int) : Event {
        try {
            val responseBody = requestWoopApi(eventId)

            if(responseBody.isNullOrEmpty())
                throw Exception("Não foi possível encontrar o evento selecionado")

            val event = Gson().fromJson<Event>(responseBody, Event::class.java)
            return event
        }
        catch ( ex : Exception ) {
            ex.printStackTrace()
            throw ex
        }
    }

    private fun requestWoopApi(eventId : Int?) : String? {
        try {
            val woopApi : WoopApi = WoopApi()
            var customEndPoint = endPoint
            if (eventId != null)
                customEndPoint = "$endPoint/$eventId"

            woopApi.GET(customEndPoint)

            if (woopApi.error != null)
                throw Exception("Falha ao obter dados do servidor")

            return woopApi.responseBody
        }
        catch ( ex : Exception ) {
            ex.printStackTrace()
            throw ex
        }
    }

}