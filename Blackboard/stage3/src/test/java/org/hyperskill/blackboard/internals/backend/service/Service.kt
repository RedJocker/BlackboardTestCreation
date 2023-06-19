package org.hyperskill.blackboard.internals.backend.service

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

interface Service {
    fun serve(request: RecordedRequest): MockResponse
}