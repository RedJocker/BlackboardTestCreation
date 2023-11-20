package org.hyperskill.blackboard.internals

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.util.concurrent.TimeUnit

open class BlackboardUnitTest<T : Activity>(clazz: Class<T>): AbstractUnitTest<T>(clazz) {

    companion object {
        const val TEACHER_SCREEN_NAME = "TeacherFragment"
        const val TEACHER_STUDENT_SCREEN_NAME = "TeacherStudentDetailsFragment"
        const val STUDENT_SCREEN_NAME = "StudentFragment"
    }


    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val baseUrlArg by lazy {
        Intent().apply {
            putExtra("baseUrl", baseUrlMockWebServer)
        }
    }

    val invalidBaseUrlArg by lazy {
        Intent().apply {
            putExtra("baseUrl", "http://invalid/url/")
        }
    }

    lateinit var mockWebServer: MockWebServer
    lateinit var baseUrlMockWebServer: String
    lateinit var blackBoardMockBackEnd: BlackBoardMockBackEnd

    internal fun TextView.assertText(
        expectedText: String,
        idString: String,
        caseDescription: String) {

        val actualText = text.toString()
        val message = "$caseDescription, on property text of TextView with id $idString"

        assertEquals(message, expectedText, actualText)
    }

    internal fun Button.assertText(
        expectedText: String,
        idString: String,
        caseDescription: String) {

        val actualText = text.toString()
        val message = "$caseDescription, on property text of Button with id $idString"

        assertEquals(message, expectedText, actualText)
    }

    internal fun EditText.assertText(
        expectedText: String,
        idString: String,
        caseDescription: String) {

        val actualText = text.toString()
        val message = "$caseDescription, on property text of EditText with id $idString"
        assertEquals(message, expectedText, actualText)
    }

    internal fun EditText.assertError(
        expectedError: String?,
        idString: String,
        caseDescription: String) {

        val actualError = error?.toString()
        val message = "$caseDescription, on property error of EditText with id '$idString'"
        assertEquals(message, expectedError, actualError)
    }

    internal fun TextView.assertError(
        expectedError: String?,
        idString: String,
        caseDescription: String) {

        val actualIsFocusable = isFocusable
        val errorMessageIsFocusable =
            "Expected View with id '$idString' to set property 'isFocusable'"
        assertEquals(errorMessageIsFocusable, true, actualIsFocusable)

        val actualIsFocusableInTouchMode = isFocusableInTouchMode
        val errorMessageIsFocusableInTouchMode =
            "Expected View with id '$idString' to set property 'isFocusableInTouchMode'"
        assertEquals(errorMessageIsFocusableInTouchMode, true, actualIsFocusableInTouchMode)

        val actualIsFocused = isFocused
        val errorMessageIsFocused =
            "$caseDescription, View with id '$idString' should be focused"
        assertEquals(errorMessageIsFocused, true, actualIsFocused)

        val actualError = error?.toString()
        val message = "$caseDescription, on property error of TextView with id $idString"
        assertEquals(message, expectedError, actualError)
    }

    internal fun EditText.assertFocus(
        expectedIsFocused: Boolean,
        idString: String,
        caseDescription: String) {

        val actualIsFocused = isFocused
        val message = "$caseDescription, on property isFocused of EditText with id $idString"
        assertEquals(message, expectedIsFocused, actualIsFocused)
    }

    internal fun EditText.assertHint(
        expectedHint: String,
        idString: String,
        caseDescription: String) {

        val actualHint = hint.toString()
        val message = "$caseDescription, on property hint of EditText with id $idString"
        assertEquals(message, expectedHint, actualHint)
    }

    internal fun EditText.assertInputType(
        expectedInputType: Int,
        inputTypeString: String,
        idString: String,
        caseDescription: String) {

        val actualInputType = inputType

        val message = "$caseDescription, on property inputType of EditText with id $idString " +
                "expected $inputTypeString($expectedInputType), but was _($actualInputType)"
        assertTrue(message, expectedInputType == actualInputType)
    }

    internal fun EditText.assertValues(
        expectedText: String,
        expectedHint: String,
        expectedInputType: Int,
        inputTypeString: String,
        idString: String,
        caseDescription: String) {
        assertText(
            expectedText = expectedText,
            idString = idString,
            caseDescription = caseDescription)
        assertHint(
            expectedHint = expectedHint,
            idString = idString,
            caseDescription = caseDescription)
        assertInputType(
            expectedInputType = expectedInputType,
            inputTypeString = inputTypeString,
            idString = idString,
            caseDescription = caseDescription)
    }

    fun assertGetRequestWithToken(caseDescription: String, token: String, path: String) {
        val request = mockWebServer.takeRequest(10L, TimeUnit.SECONDS)
        org.junit.Assert.assertNotNull(
            "$caseDescription expected a request to be sent",
            request
        )

        assertEquals("$caseDescription. Wrong request method", "GET", request!!.method)

        assertEquals(
            "$caseDescription. Wrong endpoint on request",
            path.removeSuffix("/"),
            request.path?.removeSuffix("/")
        )
        val authHeader = "Authorization"
        val actualTokenHeader = request.getHeader(authHeader)
        val expectedTokenHeader = "Bearer $token"

        val messageTokenHeader = "$caseDescription. Expected $authHeader header on request for ${request.path}"
        assertEquals(messageTokenHeader, expectedTokenHeader, actualTokenHeader)
        shadowLooper.runToEndOfTasks()
    }

    fun assertPatchRequestWithToken(caseDescription: String, token: String, path: String) {
        val request = mockWebServer.takeRequest(10L, TimeUnit.SECONDS)
        org.junit.Assert.assertNotNull(
            "$caseDescription expected a request to be sent",
            request
        )

        assertEquals("$caseDescription. Wrong request method", "PATCH", request!!.method)

        assertEquals(
            "$caseDescription. Wrong endpoint on request",
            path.removeSuffix("/"),
            request.path?.removeSuffix("/")
        )
        val authHeader = "Authorization"
        val actualTokenHeader = request.getHeader(authHeader)
        val expectedTokenHeader = "Bearer $token"

        val messageTokenHeader = "$caseDescription. Expected $authHeader header on request for ${request.path}"
        assertEquals(messageTokenHeader, expectedTokenHeader, actualTokenHeader)

        shadowLooper.runToEndOfTasks()
    }

    fun assertPatchResponse(caseDescription: String, student: Student) {
        val gradesAdapter = moshi.adapter(Grades::class.java)
        val response = blackBoardMockBackEnd.poolResponse()

        val messageUnexpectedResponse =
            "$caseDescription got unexpected response status, check you are doing the right request, response "
        assertEquals(messageUnexpectedResponse, "HTTP/1.1 200 OK", response.status)
        val expectedBody = gradesAdapter.toJson(student.grades)
        val messageUnexpectedBody =
            "$caseDescription got unexpected response body, check you are doing the right request, response "
        assertEquals(messageUnexpectedBody, expectedBody , response.getBody()?.readUtf8())
    }
}