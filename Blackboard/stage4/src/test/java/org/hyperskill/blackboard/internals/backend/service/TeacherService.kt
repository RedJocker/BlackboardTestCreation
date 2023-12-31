package org.hyperskill.blackboard.internals.backend.service

import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.GEORGE
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.users
import org.hyperskill.blackboard.internals.backend.dto.StudentsNamesResponse
import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.model.Teacher
import org.hyperskill.blackboard.internals.backend.response.Response
import org.hyperskill.blackboard.internals.backend.response.Response.withBody

class TeacherService(val moshi: Moshi): Service {
    val responseAdapter = moshi.adapter(StudentsNamesResponse::class.java)
    val responseGradesAdapter = moshi.adapter(Grades::class.java)

    override fun serve(request: RecordedRequest): MockResponse {
        println(request)
        return when(val path = request.path ?: "") {
            "/teacher/student/" -> {
                if (request.method == "GET") {
                    val teacher = users[GEORGE] as? Teacher

                    val authHeader = "Authorization"
                    val actualToken = request.getHeader(authHeader)
                    val expectedToken = "Bearer ${teacher?.token}"

                    if (teacher == null) {
                        Response.notFound404
                    } else if (actualToken != expectedToken) {
                        Response.forbidden
                    } else {
                        val studentsNames = users.values
                            .filterIsInstance<Student>()
                            .map { StudentsNamesResponse.Names(it.username) }
                        val studentsNamesResponse = StudentsNamesResponse(studentsNames)
                        Response.ok200.withBody(responseAdapter.toJson(studentsNamesResponse))
                    }
                } else {
                    Response.notFound404
                }
            }
            else -> {
                val name = path
                    .substringAfter("/teacher/student/", "")
                    .removeSuffix("/")
                val student = users[name] as? Student
                val teacher = users[GEORGE] as Teacher
                val authHeader = "Authorization"
                val actualToken = request.getHeader(authHeader)
                val expectedToken = "Bearer ${teacher.token}"

                if(student == null) {
                    Response.notFound404
                } else if(actualToken != expectedToken) {
                    Response.forbidden
                } else {
                    if (request.method == "GET") {
                        Response.ok200.withBody(responseGradesAdapter.toJson(student.grades))
                    } else if (request.method == "PATCH") {

                        val payloadString = request.body.readUtf8()
                        val maybePayload = responseGradesAdapter.fromJson(payloadString)

                        if(maybePayload == null) {
                            Response.badRequest400
                        } else {
                            Response.ok200.withBody(payloadString)
                        }
                    } else {
                        Response.notFound404
                    }
                }
            }
        }
    }
}
