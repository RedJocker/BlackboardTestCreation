package org.hyperskill.blackboard

import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.GEORGE
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.LUCAS
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Stage2UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){


    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        blackBoardMockBackEnd = BlackBoardMockBackEnd(moshi)
        mockWebServer.dispatcher = blackBoardMockBackEnd
        baseUrlMockWebServer = mockWebServer.url("/").toString()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


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

    @Test
    fun test04_checkInternetPermission() {
        testActivity {
            assertInternetPermission()
        }
    }

    @Test
    fun test05_checkValidSubmissionRoleTeacher() {
        val teacher = MockUserDatabase.users[GEORGE]!!

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(teacher.username, teacher.plainPass)
                assertLoginRequestSuccess()

                val caseDescription = "With correct ${teacher.role} ${teacher.username} login"
                assertToastTeacherLoginSuccess(teacher.username, caseDescription)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test06_checkValidSubmissionRoleStudent() {
        val student = MockUserDatabase.users[LUCAS]!!

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                assertLoginRequestSuccess()

                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
        }
    }



    //TODO
    // test invalid login
    // test login network failure
}