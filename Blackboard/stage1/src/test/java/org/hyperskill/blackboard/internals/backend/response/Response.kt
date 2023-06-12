package org.hyperskill.blackboard.internals.backend.response

import okhttp3.mockwebserver.MockResponse

object Response {

    val notFound404 get() = mockResponseOf("HTTP/1.1 404 Not Found")
    val gatewayTimeout504 get() = mockResponseOf("HTTP/1.1 504 Gateway Timeout")
    val badRequest400 get() = mockResponseOf("HTTP/1.1 400 Bad Request")
    val unauthorized401 get() = mockResponseOf("HTTP/1.1 401 Unauthorized")
    val ok200 get() = mockResponseOf("HTTP/1.1 200 OK")


    fun mockResponseOf(status: String): MockResponse {
        println("mock $status")
        return MockResponse().apply { this.status = status }
    }

    fun MockResponse.withBody(body: String): MockResponse {
        println("mock $status $body")
        return apply {
            setBody(body)
            addHeader("Content-Type", "application/json")
        }
    }
}