package org.hyperskill.blackboard

import okhttp3.internal.closeQuietly
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.GEORGE
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.hyperskill.blackboard.internals.screen.TeacherScreen
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Stage4UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){

    companion object {
        private const val TEACHER_SCREEN_NAME = "TeacherFragment"
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
    fun test00_checkTeacherGeorgeStudentList() {
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
            TeacherScreen(this, TEACHER_SCREEN_NAME).apply {
                val caseDescription = "After teacher $name login"
                assertGetRequestWithToken(caseDescription, teacher.token)
                val expectedStudentsList : List<Student> = MockUserDatabase.users
                    .values
                    .filterIsInstance<Student>()
                assertStudentList(expectedStudentsList, caseDescription)
            }
        }
    }

    // TODO
    // test get teacher/student details
    // test patch teacher/student details
    // test grades logic in teacher/student details
}