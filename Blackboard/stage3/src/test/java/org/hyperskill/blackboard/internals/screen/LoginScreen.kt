package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.screen.BlackboardTitle.Companion.BLACKBOARD_TITLE_ID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.robolectric.shadows.ShadowToast
import java.util.concurrent.TimeUnit

class LoginScreen<T: Activity>(val test: BlackboardUnitTest<T>, initViews: Boolean = true) {

    companion object {
        const val DESCRIPTION_INITIALIZATION = "On LoginFragment initialization"
        const val LOGIN_USERNAME_ET_ID = "login_username_et"
        const val LOGIN_PASS_ET_ID = "login_pass_et"
        const val LOGIN_SUBMIT_BTN_ID = "login_submit_btn"
    }

    val blackboardTitle by lazy {
        BlackboardTitle("LoginFragment", test).blackboardTitle
    }

    val loginUsernameEt: EditText by lazy {
        with(test) {
            activity.findViewByString<EditText>(LOGIN_USERNAME_ET_ID).apply {
                val textPersonNameType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                assertValues(
                    expectedText = "",
                    expectedHint = "username",
                    expectedInputType = textPersonNameType,
                    inputTypeString = "textPersonName",
                    idString = LOGIN_USERNAME_ET_ID,
                    caseDescription = DESCRIPTION_INITIALIZATION
                )
            }
        }
    }
    val loginPassEt: EditText by lazy {
        with(test) {
            activity.findViewByString<EditText>(LOGIN_PASS_ET_ID).apply {

                val textPasswordType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                assertValues(
                        expectedText = "",
                        expectedHint = "password",
                        expectedInputType = textPasswordType,
                        inputTypeString = "textPassword",
                        idString = LOGIN_PASS_ET_ID,
                        caseDescription = DESCRIPTION_INITIALIZATION
                )
            }
        }
    }
    val loginSubmitBt: Button by lazy {
        with(test) {
            activity.findViewByString<Button>(LOGIN_SUBMIT_BTN_ID).apply {
                assertText(
                    expectedText = "SUBMIT",
                    idString = BLACKBOARD_TITLE_ID,
                    caseDescription = DESCRIPTION_INITIALIZATION
                )
            }
        }
    }

    init {
        if(initViews){
            blackboardTitle
            loginSubmitBt
            loginPassEt
            loginUsernameEt
        }
    }

    fun fillLogin(username: String, plainPass: String) = with(test) {
        loginUsernameEt.setText(username)
        loginPassEt.setText(plainPass)
        loginSubmitBt.clickAndRun()
    }

    fun assertToastTeacherLoginSuccess(teacherUsername: String, caseDescription: String) = with(test) {
        assertToastLoginSuccess(
            expectedMessage = "Good day teacher $teacherUsername",
            caseDescription = caseDescription)
    }

    fun assertToastStudentLoginSuccess(studentUsername: String, caseDescription: String) = with(test) {
        assertToastLoginSuccess(
                expectedMessage = "Hello $studentUsername",
                caseDescription = caseDescription)
    }

    private fun assertToastLoginSuccess(expectedMessage: String, caseDescription: String) = with(test) {
        assertLastToastMessageEquals(
                errorMessage = "$caseDescription expected toast message with text",
                expectedMessage = expectedMessage)
        ShadowToast.reset()
    }

    fun assertLoginSuccessClearInput() = with(test) {
        val caseDescription = "After successful login input should be cleared"
        loginUsernameEt.assertText(
                expectedText = "",
                idString = LOGIN_USERNAME_ET_ID,
                caseDescription = caseDescription)

        loginPassEt.assertText(
                expectedText = "",
                idString = LOGIN_PASS_ET_ID,
                caseDescription = caseDescription)
    }

    fun assertLoginInvalid(username: String, caseDescription: String) = with(test) {

        loginUsernameEt.apply {
            assertError(
                expectedError = "Invalid Login",
                idString = LOGIN_USERNAME_ET_ID,
                caseDescription = "$caseDescription should set error")

            assertText(
                expectedText = username,
                idString = LOGIN_USERNAME_ET_ID,
                caseDescription = "$caseDescription should keep username")

            assertFocus(expectedIsFocused = true, LOGIN_USERNAME_ET_ID, caseDescription)
        }
        loginPassEt.apply {
            assertError(expectedError = null, LOGIN_PASS_ET_ID, caseDescription)
            assertText("", LOGIN_PASS_ET_ID, "$caseDescription should clear password")
            assertFocus(expectedIsFocused = false, LOGIN_PASS_ET_ID, caseDescription)
        }
    }

    fun refillLoginPassOnlyAndAssertErrorMessageCleared(
            plainPass: String, caseDescription: String) = with(test) {

        loginPassEt.setText(plainPass)
        loginUsernameEt.assertError(null, LOGIN_USERNAME_ET_ID, caseDescription)
        loginSubmitBt.clickAndRun()
    }

    fun assertLoginRequestOk(caseDescription: String) {
        assertLoginRequest(caseDescription, expectedResponseStatus = "HTTP/1.1 200 OK")
    }

    fun assertLoginRequestUnauthorized(caseDescription: String) {
        assertLoginRequest(caseDescription, expectedResponseStatus = "HTTP/1.1 401 Unauthorized")
    }

    private fun assertLoginRequest(caseDescription: String, expectedResponseStatus: String) = with(test) {
        val request = mockWebServer.takeRequest(10L, TimeUnit.SECONDS)
        assertNotNull(
            "$caseDescription expected a request to be sent",
            request
        )
        request!!
        assertEquals("Wrong request method", "POST", request.method)
        assertEquals("Wrong request path", "/login", request.path)


        val loginResponse = blackBoardMockBackEnd.poolResponse()

        val messageUnexpectedResponse =
            "$caseDescription got unexpected response status, check you are doing the right request, response "
        assertEquals(messageUnexpectedResponse, expectedResponseStatus, loginResponse.status)

        Thread.sleep(150)           // Callback.onResponse is async
        shadowLooper.runToEndOfTasks()  // runOnUiThread goes to Handler queue
    }

    fun assertLoginNetworkError(caseDescription: String, expectedError: String) = with(test) {
        Thread.sleep(150)           // Callback.onResponse is async
        shadowLooper.runToEndOfTasks()  // runOnUiThread goes to Handler queue

        blackboardTitle.assertError(expectedError, BLACKBOARD_TITLE_ID, caseDescription)
        loginUsernameEt.assertError(null, LOGIN_USERNAME_ET_ID, caseDescription)
        loginPassEt.assertError(null, LOGIN_PASS_ET_ID, caseDescription)
    }
}