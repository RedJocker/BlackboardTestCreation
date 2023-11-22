package org.hyperskill.blackboard

import okhttp3.internal.closeQuietly
import okhttp3.mockwebserver.MockWebServer
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.BlackBoardMockBackEnd
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.BENSON
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.MARTIN
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase.ORWELL
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
                    assertGradesEditTextEnabledDisabled(caseDescription, student)
                    activity.clickBackAndRun()
                }
            }
        }
    }

    @Test
    fun test01_checkOrwellRemoveGradeAfterGivingOneHundred() {

        val orwell = MockUserDatabase.users[ORWELL] as Student
        val orwell100 = orwell.copy(grades = orwell.grades.copy(exam = 100))

        testActivity(arguments = baseUrlArg) {

            LoginScreen(this).apply {
                fillLogin(orwell.username, orwell.plainPass)
                val caseDescription = "With correct ${orwell.role} ${orwell.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(orwell.username, caseDescription)
                assertLoginSuccessClearInput()
            }

            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                mockWebServer.takeRequest()

                val caseDescriptionBefore = "For student ${orwell.username} before changing exam to 100"
                assertGradesEditTextEnabledDisabled(caseDescriptionBefore, orwell)
                assertStudentDetails(orwell, caseDescriptionBefore)

                val caseDescriptionAfterChangingTo100 = "For student ${orwell.username} after changing exam to 100"
                editExamChange(orwell100)
                assertExam(orwell100, caseDescriptionAfterChangingTo100)
                assertCalculationWithPrediction(orwell, orwell100, caseDescriptionAfterChangingTo100)

                val caseDescriptionAfterRemoving100 = "For student ${orwell.username} after changing exam from 100 to empty"
                editExamChangeWithString("")
                assertExam(orwell, caseDescriptionAfterRemoving100)
                assertStudentDetails(orwell, caseDescriptionAfterRemoving100)
            }
        }
    }

    @Test
    fun test02_checkBensonRemoveGradeAfterGivingOneHundred() {

        val benson = MockUserDatabase.users[BENSON] as Student
        val benson100 = benson.copy(grades = benson.grades.copy(exam = 100))

        testActivity(arguments = baseUrlArg) {

            LoginScreen(this).apply {
                fillLogin(benson.username, benson.plainPass)
                val caseDescription = "With correct ${benson.role} ${benson.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastStudentLoginSuccess(benson.username, caseDescription)
                assertLoginSuccessClearInput()
            }

            StudentScreen(this, STUDENT_SCREEN_NAME).apply {
                mockWebServer.takeRequest()

                val caseDescriptionBefore = "For student ${benson.username} before changing exam to 100"
                assertGradesEditTextEnabledDisabled(caseDescriptionBefore, benson)
                assertStudentDetails(benson, caseDescriptionBefore)

                val caseDescriptionAfterChangingTo100 = "For student ${benson.username} after changing exam to 100"
                editExamChange(benson100)
                assertExam(benson100, caseDescriptionAfterChangingTo100)
                assertCalculationWithPrediction(benson, benson100, caseDescriptionAfterChangingTo100)

                val caseDescriptionAfterRemoving100 = "For student ${benson.username} after changing exam from 100 to empty"
                editExamChangeWithString("")
                assertExam(benson, caseDescriptionAfterRemoving100)
                assertStudentDetails(benson, caseDescriptionAfterRemoving100)
            }
        }
    }
}