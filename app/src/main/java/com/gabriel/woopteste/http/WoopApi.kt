package com.gabriel.woopteste.http

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class WoopApi {
    private val BASE_URL = URL("http://5f5a8f24d44d640016169133.mockapi.io/api/")
    private val okHttpclient = OkHttpClient()

    var responseBody : String? = null
    var error : Exception? = null

    fun GET(endpoint: String) {
        try {
            val url = "$BASE_URL$endpoint"

            val request = Request.Builder()
                .url(url)
                .build()

            okHttpclient.newCall(request).execute().use { response ->
                if (!response.isSuccessful)
                    throw Exception()

                if( response.body != null)
                    responseBody = response.body!!.string()
            }
        }
        catch (ex : Exception) {
            ex.printStackTrace()
            error = ex
        }
    }

    fun POST(endpoint: String, body : String) {
        try {
            val url = "$BASE_URL$endpoint"

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody(MEDIA_TYPE_MARKDOWN))
                .build()

            okHttpclient.newCall(request).execute().use { response ->
                if (!response.isSuccessful)
                    throw Exception()

                if( response.body != null)
                    responseBody = response.body!!.string()
            }
        }
        catch (ex : Exception) {
            ex.printStackTrace()
            error = ex
        }
    }

    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
    }
}