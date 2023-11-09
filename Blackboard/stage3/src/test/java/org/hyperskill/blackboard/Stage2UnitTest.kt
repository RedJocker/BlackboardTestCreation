package org.hyperskill.blackboard

import okhttp3.internal.closeQuietly
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.GEORGE
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.LUCAS
import org.hyperskill.blackboard.internals.backend.model.Teacher
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.hyperskill.blackboard.internals.shadows.CustomShadowAsyncDifferConfig
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Config(shadows = [CustomShadowAsyncDifferConfig::class])
class Stage2UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){


    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        blackBoardMockBackEnd = BlackBoardMockBackEnd(moshi)
        mockWebServer.dispatcher = blackBoardMockBackEnd
        baseUrlMockWebServer = mockWebServer.url("/").toString()
        println("baseUrlMockWebServer $baseUrlMockWebServer")
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        mockWebServer.closeQuietly()
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

                val caseDescription = "With correct ${teacher.role} ${teacher.username} login"
                assertLoginRequestOk(caseDescription)
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

                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test07_checkInvalidSubmissionWithNoInputGiven() {

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                val caseDescription = "After clicking submit without giving any input"
                loginSubmitBt.clickAndRun()
                assertLoginRequestUnauthorized(caseDescription)
                assertLoginInvalid(username = "", caseDescription = caseDescription)
            }
        }
    }

    @Test
    fun test08_checkInvalidSubmissionWithEmptyInputGiven() {

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                val caseDescription =
                    "After clicking submit with empty strings for username and password"
                fillLogin("", "")
                assertLoginRequestUnauthorized(caseDescription)
                assertLoginInvalid(username = "", caseDescription = caseDescription)
            }
        }
    }

    @Test
    fun test09_checkInvalidSubmissionWithUnknownUser() {
        val unknownUser = Teacher("Jose", "asdf")

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                val caseDescription = "After clicking submit with unknown user"
                fillLogin(unknownUser.username, unknownUser.plainPass)
                assertLoginRequestUnauthorized(caseDescription)
                assertLoginInvalid(username = unknownUser.username, caseDescription = caseDescription)
            }
        }
    }

    @Test
    fun test10_checkTeacherLoginRefillPassOnlyAfterInvalidPass() {
        val teacher = MockUserDatabase.users[GEORGE]!!


        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(teacher.username, teacher.plainPass.take(1))

                val caseDescriptionInvalidPass = "${teacher.role} ${teacher.username} with invalid pass"
                assertLoginRequestUnauthorized(caseDescriptionInvalidPass)
                assertLoginInvalid(username = teacher.username, caseDescription = caseDescriptionInvalidPass)

                val caseDescriptionRetryLoginPassOnly =
                    "After invalid pass with existing username correcting pass only"
                refillLoginPassOnlyAndAssertErrorMessageCleared(
                    teacher.plainPass, caseDescriptionRetryLoginPassOnly
                )

                val caseDescriptionRetrySuccess =
                    "On retry login and correct ${teacher.role} ${teacher.username} login"
                assertLoginRequestOk(caseDescriptionRetrySuccess)
                assertToastTeacherLoginSuccess(teacher.username, caseDescriptionRetrySuccess)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test11_checkStudentLoginRefillPassOnlyAfterInvalidPass() {
        val student = MockUserDatabase.users[LUCAS]!!

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass + "nope")

                val caseDescriptionInvalidPass =
                    "${student.role} ${student.username} with invalid pass"
                assertLoginRequestUnauthorized(caseDescriptionInvalidPass)
                assertLoginInvalid(username = student.username, caseDescription = caseDescriptionInvalidPass)

                val caseDescriptionRetryLoginPassOnly =
                    "After invalid pass with existing username correcting pass only"
                refillLoginPassOnlyAndAssertErrorMessageCleared(
                    student.plainPass, caseDescriptionRetryLoginPassOnly
                )

                val caseDescriptionRetrySuccess =
                    "On retry login and correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescriptionRetrySuccess)
                assertToastStudentLoginSuccess(student.username, caseDescriptionRetrySuccess)
                assertLoginSuccessClearInput()
            }
        }
    }


    @Test
    fun test12_checkNetworkError() {
        val student = MockUserDatabase.users[LUCAS]!!

        testActivity(arguments = invalidBaseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With invalid baseUrl leading to network error"
                val expectedError = "invalid: nodename nor servname provided, or not known"
                assertLoginNetworkError(caseDescription, expectedError = expectedError)
            }
        }
    }
}