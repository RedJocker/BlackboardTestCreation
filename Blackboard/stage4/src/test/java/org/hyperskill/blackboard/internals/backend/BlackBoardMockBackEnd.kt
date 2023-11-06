package org.hyperskill.blackboard.internals.backend

import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hyperskill.blackboard.internals.backend.response.Response
import org.hyperskill.blackboard.internals.backend.service.LoginService
import org.hyperskill.blackboard.internals.backend.service.StudentService
import org.hyperskill.blackboard.internals.backend.service.TeacherService

class BlackBoardMockBackEnd(moshi: Moshi) : Dispatcher() {

    private val loginService = LoginService(moshi)
    private val studentService = StudentService(moshi)
    private val teacherService = TeacherService(moshi)
    private val responseList = mutableListOf<MockResponse>()

    override fun dispatch(request: RecordedRequest): MockResponse {
        println("dispatch $request")
        return controller(request)
                .also { responseList.add(it) }
    }

    fun controller(request: RecordedRequest): MockResponse {
        return when {
            request.path == null -> Response.badRequest400
            request.path == "/login" -> loginService.serve(request)
            request.path?.startsWith("/teacher/student/") ?: false -> teacherService.serve(request)
            request.path?.startsWith("/student") ?: false -> studentService.serve(request)
            else -> Response.notFound404
        }
    }

    fun poolResponse() : MockResponse {
        var response = responseList.firstOrNull()
        var tries = 0
        while(response == null) {
            Thread.sleep(50)
            response = responseList.firstOrNull()
            tries++
            if(tries > 15) {
                throw AssertionError("Test was not able to retrieve a response in time")
            }
        }
        println("pooling tries $tries")
        return response.also { responseList.removeAt(0) }
    }

    @Suppress("UNUSED")
    fun clearResponseList() {
        responseList.clear()
    }
}