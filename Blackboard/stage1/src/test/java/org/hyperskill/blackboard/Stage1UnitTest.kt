package org.hyperskill.blackboard

import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Stage1UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){

    @Test
    fun test00_checkTitleTv() {
        testActivity {
            LoginScreen(this, initViews = false).apply {
                blackboardTitle
            }
        }
    }

    @Test
    fun test01_checkUsernameEt() {
        testActivity {
            LoginScreen(this, initViews = false).apply {
                loginUsernameEt
            }
        }
    }

    @Test
    fun test02_checkPasswordEt() {
        testActivity {
            LoginScreen(this, initViews = false).apply {
                loginPassEt
            }
        }
    }

    @Test
    fun test03_checkSubmitBtn() {
        testActivity {
            LoginScreen(this, initViews = false).apply {
                loginSubmitBt
            }
        }
    }
}