package org.hyperskill.blackboard

import okhttp3.internal.closeQuietly
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.MARTIN
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.hyperskill.blackboard.internals.screen.StudentScreen
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
class Stage5UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){

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
    fun test00_checkEditTextAllStudentsExceptMartin() {

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
            .filter { it.username != MARTIN }

        testActivity(arguments = baseUrlArg) {
            students.forEachIndexed { studentIndex, student ->

                LoginScreen(this).apply {
                    fillLogin(student.username, student.plainPass)
                    val caseDescription = "With correct ${student.role} ${student.username} login"
                    assertLoginRequestOk(caseDescription)
                    assertToastStudentLoginSuccess(student.username, caseDescription)
                    assertLoginSuccessClearInput()
                }

                StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                    mockWebServer.takeRequest()
                    val caseDescription = "For student ${student.username}"
                    this.assertGradesEditTextEnabledDisabled(caseDescription, student)
                    activity.clickBackAndRun()
                }
            }
        }
    }
}