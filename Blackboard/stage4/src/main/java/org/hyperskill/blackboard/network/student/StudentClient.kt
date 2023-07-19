package org.hyperskill.blackboard.network.student

import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.BaseClient
import org.hyperskill.blackboard.network.student.dto.GradesResponse

class StudentClient(client: OkHttpClient, moshi: Moshi): BaseClient(client, moshi) {

    fun fetchGrades(credential: Credential, callback: Callback): Call {
        return client.newCall(fetchGradesRequest(credential)).also {
            it.enqueue(callback)
        }
    }

    private fun fetchGradesRequest(credential: Credential): Request {
        val url = baseurl.toHttpUrl().resolve("student/${credential.username}")!!
        println(url.toString())
        return Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${credential.token}")
            .build()
    }

    fun parseGrades(body: ResponseBody?): GradesResponse? {
        return body?.let {
            moshi.adapter(GradesResponse.Success::class.java)
                .fromJson(body.source())
                ?: GradesResponse.Fail("Server Error", 500)
        }
    }
}