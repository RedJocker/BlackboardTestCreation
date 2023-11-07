package org.hyperskill.blackboard.network.login

import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.hyperskill.blackboard.network.BaseClient
import org.hyperskill.blackboard.network.login.dto.LoginRequest
import org.hyperskill.blackboard.network.login.dto.LoginResponse

class LoginClient(client: OkHttpClient, moshi: Moshi): BaseClient(client, moshi) {

    fun loginRequest(username: String, pass: String, callback: Callback): Call {
        return client.newCall(createLoginRequest(username, pass))
            .also { call -> call.enqueue(callback) }

    }

    private fun createLoginRequest(username: String, pass: String): Request {
        val mediaType = "application/json".toMediaType()
        val body = moshi.adapter(LoginRequest::class.java)
            .toJson(LoginRequest(username, pass))
            .toRequestBody(mediaType)
        val url = baseurl.toHttpUrl().resolve("login")!!
        println(url.toString())
        return Request.Builder()
            .url(url)
            .post(body)
            .build()
    }

    fun parse(body: ResponseBody): LoginResponse {
        return moshi.adapter(LoginResponse.Success::class.java)
            .fromJson(body.source())
            ?: LoginResponse.Fail("Server Error", 500)
    }
}