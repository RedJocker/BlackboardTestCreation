package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.robolectric.shadows.ShadowToast

class LoginScreen<T: Activity>(val test: BlackboardUnitTest<T>, initViews: Boolean = true) {

    companion object {
        const val DESCRIPTION_INITIALIZATION = "On LoginFragment initialization"
        const val LOGIN_USERNAME_ET_ID = "login_username_et"
        const val LOGIN_PASS_ET_ID = "login_pass_et"
        const val LOGIN_SUBMIT_BTN_ID = "login_submit_btn"
    }

    val blackboardTitle by lazy {
        BlackboardTitle("LoginFragment", test)
    }

    val loginUsernameEt: EditText by lazy {
        with(test) {
            activity.findViewByString<EditText>(LOGIN_USERNAME_ET_ID).apply {
                assertValues(
                    expectedText = "",
                    expectedHint = "username",
                    expectedInputType = 97,
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
                assertValues(
                        expectedText = "",
                        expectedHint = "password",
                        expectedInputType = 129,
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
                    idString = BlackboardTitle.BLACKBOARD_TITLE_ID,
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
}