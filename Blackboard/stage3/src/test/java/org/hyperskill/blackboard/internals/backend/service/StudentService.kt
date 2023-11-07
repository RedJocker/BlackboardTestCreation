package org.hyperskill.blackboard.internals.backend.service

import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.response.Response
import org.hyperskill.blackboard.internals.backend.response.Response.withBody


class StudentService(val moshi: Moshi): Service {

    val responseAdapter = moshi.adapter(Grades::class.java)
    override fun serve(request: RecordedRequest): MockResponse {
        println(request)

        return if (request.method == "GET") {
            when(val path = request.path ?: "") {
                "/student/Martin",
                "/student/Martin/"-> Response.gatewayTimeout504
                else -> {
                    val name = path
                        .substringAfter("/student/", "")
                        .removeSuffix("/")
                    val student = MockUserDatabase.users[name] as? Student

                    val authHeader = "Authorization"
                    val actualToken = request.getHeader(authHeader)
                    val expectedToken = "Bearer ${student?.token}"

                    if(student == null) {
                        Response.notFound404
                    } else if(actualToken != expectedToken) {
                        Response.forbidden
                    } else {
                        Response.ok200.withBody(responseAdapter.toJson(student.grades))
                    }
                }
            }
        } else {
            Response.notFound404
        }
    }
}