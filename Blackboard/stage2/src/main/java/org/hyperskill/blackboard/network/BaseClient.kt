package org.hyperskill.blackboard.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient

open class BaseClient(protected val client: OkHttpClient, protected val moshi: Moshi) {
    companion object {
        var baseurl = "http://10.0.2.2:3001/"
    }
}