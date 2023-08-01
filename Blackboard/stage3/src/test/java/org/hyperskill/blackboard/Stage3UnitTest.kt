package org.hyperskill.blackboard

import android.view.View
import okhttp3.internal.closeQuietly
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.BENSON
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.GEORGE
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.HARRISON
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.LUCAS
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.MARTIN
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.MICHAEL
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.ORWELL
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.model.User
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.hyperskill.blackboard.internals.screen.StudentScreen
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Stage3UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){

    companion object {
        private const val STUDENT_SCREEN_NAME = "StudentFragment"
    }

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
    fun test00_checkStudentScreen() {
        val student = MockUserDatabase.users[LUCAS]!!

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            StudentScreen(this, STUDENT_SCREEN_NAME).apply {

            }
        }
    }

    @Test
    fun test01_checkStudentLucas() {
        val name = LUCAS
        val student = MockUserDatabase.users[name]!! as Student

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                val caseDescription = "After student $name login"
                assertGetRequestWithToken(caseDescription, student.token)
                assertStudentDetails(student, caseDescription)
            }
        }
    }

    @Test
    fun test02_checkStudentHarrison() {
        val name = HARRISON
        val student = MockUserDatabase.users[name]!! as Student

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                val caseDescription = "After student $name login"
                assertGetRequestWithToken(caseDescription, student.token)
                assertStudentDetails(student, caseDescription)
            }
        }
    }

    @Test
    fun test03_checkStudentMichael() {
        val name = MICHAEL
        val student = MockUserDatabase.users[name]!! as Student

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                val caseDescription = "After student $name login"
                assertGetRequestWithToken(caseDescription, student.token)
                assertStudentDetails(student, caseDescription)
            }
        }
    }

    @Test
    fun test04_checkStudentOrwell() {
        val name = ORWELL
        val student = MockUserDatabase.users[name]!! as Student

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                val caseDescription = "After student $name login"
                assertGetRequestWithToken(caseDescription, student.token)
                assertStudentDetails(student, caseDescription)
            }
        }
    }

    @Test
    fun test05_checkStudentBenson() {
        val name = BENSON
        val student = MockUserDatabase.users[name]!! as Student

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                val caseDescription = "After student $name login"
                assertGetRequestWithToken(caseDescription, student.token)
                assertStudentDetails(student, caseDescription)
            }
        }
    }

    @Test
    fun test06_checkStudentMartin() {
        val name = MARTIN
        val student = MockUserDatabase.users[name]!! as Student

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With correct ${student.role} ${student.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                val caseDescription = "After student $name login"
                assertGetRequestWithToken(caseDescription, student.token)
                assertStudentNetworkError(
                        caseDescription = "When /student/Martin responds with 504 Gateway Timeout",
                        expectedError = "504 Gateway Timeout"
                )
            }
        }
    }

    @Test
    fun test07_checkTeacherGeorge() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!!

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(teacher.username, teacher.plainPass)
                val caseDescription = "With correct ${teacher.role} ${teacher.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastTeacherLoginSuccess(teacher.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            val studentNameView = activity.findViewByStringOrNull<View>(
                StudentScreen.ID_STUDENT_NAME_TV
            )
            assertTrue(
                "A user with role ${User.Role.ROLE_TEACHER} should not navigate to student screen",
                studentNameView == null
            )
        }
    }
}