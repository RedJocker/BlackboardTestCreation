package org.hyperskill.blackboard.internals.backend.service

import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.LUCAS
import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.response.Response
import org.hyperskill.blackboard.internals.backend.response.Response.withBody


class StudentService(val moshi: Moshi): Service {

    val responseAdapter = moshi.adapter(Grades::class.java)
    override fun serve(request: RecordedRequest): MockResponse {
        println(request)

        return if (request.method == "GET") {
            when(request.path) {
                "/student/Martin" -> Response.gatewayTimeout504
                "/student/Lucas" -> Response.ok200.withBody(responseAdapter.toJson(
                    (MockUserDatabase.users[LUCAS] as Student).grades
                ))
                else -> Response.notFound404

            }
        } else {
            Response.notFound404
        }
    }
}