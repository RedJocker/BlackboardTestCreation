package org.hyperskill.blackboard.network.teacher

import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Student
import org.hyperskill.blackboard.network.BaseClient
import org.hyperskill.blackboard.network.student.dto.GradesResponse
import org.hyperskill.blackboard.network.teacher.dto.GradeUpdateRequest
import org.hyperskill.blackboard.network.teacher.dto.StudentsResponse

class TeacherClient(client: OkHttpClient,moshi: Moshi) : BaseClient(client, moshi) {

    fun students(credential: Credential, callback: Callback): Call {
        return client.newCall(createStudentsRequest(credential)).also {
            it.enqueue(callback)
        }
    }

    private fun createStudentsRequest(credential: Credential): Request {
        val url = baseurl.toHttpUrl().resolve("teacher/student/")!!
        println(url.toString())
        return Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${credential.token}")
            .build()
    }

    fun parseStudents(body: ResponseBody?): StudentsResponse? {
        return body?.let {
            moshi.adapter(StudentsResponse.Success::class.java)
                .fromJson(body.source())
                ?: StudentsResponse.Fail("Server Error", 500)
        }
    }

    fun fetchStudentGrades(
            credential: Credential,
            student: Student,
            callback: Callback
    ): Call {
        return client.newCall(fetchGradesRequest(credential, student))
                .also { it.enqueue(callback) }
    }

    private fun fetchGradesRequest(credential: Credential, student: Student): Request {
        val url = baseurl.toHttpUrl().resolve("teacher/student/${student.name}/")!!
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

    fun updateGradesRequest(
        credential: Credential,
        studentName: String,
        grades: List<Int>,
        exam: Int,
        callback: Callback
    ): Call {
        return client.newCall(
                createPatchGradesRequest(credential, studentName, grades, exam)
        ).also { call -> call.enqueue(callback) }

    }

    private fun createPatchGradesRequest(
        credential: Credential,
        studentName:String,
        grades: List<Int>,
        exam: Int
    ): Request {
        val mediaType = "application/json".toMediaType()
        val body = moshi.adapter(GradeUpdateRequest::class.java)
            .toJson(GradeUpdateRequest(grades, exam))
            .toRequestBody(mediaType)

        val url = baseurl.toHttpUrl().resolve("teacher/student/$studentName/")!!
        println(url.toString())
        return Request.Builder()
            .url(url)
            .patch(body)
            .addHeader("Authorization", "Bearer ${credential.token}")
            .build()
    }
}