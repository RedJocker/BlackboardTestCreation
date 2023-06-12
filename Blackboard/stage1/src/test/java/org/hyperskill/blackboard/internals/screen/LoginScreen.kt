package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import org.hyperskill.blackboard.internals.BlackboardUnitTest

class LoginScreen<T: Activity>(test: BlackboardUnitTest<T>, initViews: Boolean = true) {

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

    // functions that are high level abstraction for testing the login screen


}