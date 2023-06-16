package org.hyperskill.blackboard

import android.content.Intent
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.model.Teacher
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import java.io.Serializable

@RunWith(RobolectricTestRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Stage1UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){

    val customTeacher = Teacher("Julian", "d90a")
    val customStudent = Student("Djonga", "CxCf", Grades(listOf(5, 3, 4, 6), 9))
    val customArgsStage1 = Intent().apply {
        val customUserMap = mapOf(
                customTeacher.username to (customTeacher.role to customTeacher.base64sha256HashPass),
                customStudent.username to (customStudent.role to customStudent.base64sha256HashPass)
        )
        putExtra("userMap", customUserMap as Serializable)
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
    fun test04_checkValidSubmissionRoleTeacherDefaultUserMap() {
        val teacher = MockUserDatabase.users[MockUserDatabase.GEORGE]!!

        testActivity {
            LoginScreen(this).apply {
                fillLogin(teacher.username, teacher.plainPass)
                val caseDescription = "With default userMap and correct teacher ${teacher.username} login"
                assertToastTeacherLoginSuccess(teacher.username, caseDescription)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test05_checkValidSubmissionRoleStudentDefaultUserMap() {
        val student = MockUserDatabase.users[MockUserDatabase.LUCAS]!!

        testActivity {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass)
                val caseDescription = "With default userMap and correct student ${student.username} login"
                assertToastStudentLoginSuccess(student.username, caseDescription)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test06_checkInvalidSubmissionWithNoInputGiven() {

        testActivity {
            LoginScreen(this).apply {
                val caseDescription = "After clicking submit without giving any input"
                loginSubmitBt.clickAndRun()
                assertLoginInvalid(username = "", caseDescription = caseDescription)
            }
        }
    }

    @Test
    fun test07_checkInvalidSubmissionWithEmptyInputGiven() {

        testActivity {
            LoginScreen(this).apply {
                val caseDescription = "After clicking submit with empty strings for username and password"
                fillLogin("", "")
                loginSubmitBt.clickAndRun()
                assertLoginInvalid(username = "", caseDescription = caseDescription)
            }
        }
    }

    @Test
    fun test08_checkInvalidSubmissionWithUnknownUser() {
        val unknownUser = Teacher("Jose", "asdf")

        testActivity {
            LoginScreen(this).apply {
                val caseDescription = "After clicking submit with unknown teacher"
                fillLogin(unknownUser.username, unknownUser.plainPass)
                loginSubmitBt.clickAndRun()
                assertLoginInvalid(username = unknownUser.username, caseDescription = caseDescription)
            }
        }
    }

    @Test
    fun test09_checkTeacherLoginRefillPassOnlyAfterInvalidPass() {
        val teacher = MockUserDatabase.users[MockUserDatabase.GEORGE]!!
        val testedRole = "teacher"

        testActivity {
            LoginScreen(this).apply {
                fillLogin(teacher.username, teacher.plainPass.take(1))

                val caseDescriptionInvalidPass = "With default userMap and $testedRole with invalid pass"
                assertLoginInvalid(username = teacher.username, caseDescription = caseDescriptionInvalidPass)

                val caseDescriptionRetryLoginPassOnly = "After invalid pass with existing username correcting pass only"
                refillLoginPassOnlyAndAssertErrorMessageCleared(
                        teacher.plainPass, caseDescriptionRetryLoginPassOnly
                )

                val caseDescriptionRetrySuccess =
                        "On retry login with default userMap and correct $testedRole ${teacher.username} login"
                assertToastTeacherLoginSuccess(teacher.username, caseDescriptionRetrySuccess)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test10_checkStudentLoginRefillPassOnlyAfterInvalidPass() {
        val student = MockUserDatabase.users[MockUserDatabase.LUCAS]!!
        val testedRole = "student"

        testActivity {
            LoginScreen(this).apply {
                fillLogin(student.username, student.plainPass + "nope")

                val caseDescriptionInvalidPass = "With default userMap and $testedRole with invalid pass"
                assertLoginInvalid(username = student.username, caseDescription = caseDescriptionInvalidPass)

                val caseDescriptionRetryLoginPassOnly = "After invalid pass with existing username correcting pass only"
                refillLoginPassOnlyAndAssertErrorMessageCleared(
                        student.plainPass, caseDescriptionRetryLoginPassOnly
                )

                val caseDescriptionRetrySuccess =
                        "On retry login with default userMap and correct $testedRole ${student.username} login"
                assertToastStudentLoginSuccess(student.username, caseDescriptionRetrySuccess)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test11_checkValidSubmissionRoleTeacherCustomUserMap() {

        testActivity(arguments = customArgsStage1) {
            LoginScreen(this).apply {
                fillLogin(customTeacher.username, customTeacher.plainPass)
                val caseDescription = "With default userMap and correct teacher ${customTeacher.username} login"
                assertToastTeacherLoginSuccess(customTeacher.username, caseDescription)
                assertLoginSuccessClearInput()
            }
        }
    }

    @Test
    fun test12_checkValidSubmissionRoleStudentCustomUserMap() {
        testActivity(arguments = customArgsStage1) {
            LoginScreen(this).apply {
                fillLogin(customStudent.username, customStudent.plainPass)
                val caseDescription = "With custom userMap and correct student ${customStudent.username} login"
                assertToastStudentLoginSuccess(customStudent.username, caseDescription)
                assertLoginSuccessClearInput()
            }
        }
    }
}