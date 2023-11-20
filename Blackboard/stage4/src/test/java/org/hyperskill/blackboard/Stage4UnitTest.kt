package org.hyperskill.blackboard

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
import org.hyperskill.blackboard.internals.backend.model.Teacher
import org.hyperskill.blackboard.internals.screen.LoginScreen
import org.hyperskill.blackboard.internals.screen.StudentScreen.Companion.ID_GRADE_VALUE_ET
import org.hyperskill.blackboard.internals.screen.StudentScreen.Companion.ID_STUDENT_EXAM_ET
import org.hyperskill.blackboard.internals.screen.TeacherScreen
import org.hyperskill.blackboard.internals.screen.TeacherScreen.Companion.PATH_TEACHER_STUDENTS
import org.hyperskill.blackboard.internals.screen.TeacherStudentScreen
import org.hyperskill.blackboard.internals.screen.TeacherStudentScreen.Companion.ID_DETAIL_SUBMIT_BTN
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
class Stage4UnitTest : BlackboardUnitTest<MainActivity>(MainActivity::class.java){



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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                val expectedStudentsList : List<Student> = MockUserDatabase.users
                    .values
                    .filterIsInstance<Student>()
                assertStudentList(expectedStudentsList, caseDescription)
            }
        }
    }

    @Test
    fun test01_checkTeacherStudentDetailAllStudents() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()

        testActivity(arguments = baseUrlArg) {
            LoginScreen(this).apply {
                fillLogin(teacher.username, teacher.plainPass)
                val caseDescription = "With correct ${teacher.role} ${teacher.username} login"
                assertLoginRequestOk(caseDescription)
                assertToastTeacherLoginSuccess(teacher.username, caseDescription)
                assertLoginSuccessClearInput()
            }
            students.forEachIndexed { studentIndex, student ->
                TeacherScreen(this, TEACHER_SCREEN_NAME).apply {
                    val caseDescription = "After teacher $name login"
                    assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                    clickStudent(studentIndex, caseDescription)
                }
                TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                    val caseDescription =
                        "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                    assertGetRequestWithToken(
                        caseDescription,
                        teacher.token,
                        "$PATH_TEACHER_STUDENTS${student.username}/"
                    )
                    assertStudentDetails(student, caseDescription)
                    activity.clickBackAndRun()
                }
            }
        }
    }

    @Test
    fun test02_checkTeacherStudentDetailExamGradeEditSubmission() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[ORWELL] as Student
        val studentIndex = students.indexOf(student)
        val updatedStudent = student.copy(grades = student.grades.copy(
            exam = 100
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate = "After editing $ID_STUDENT_EXAM_ET"
                editExamChange(updatedStudent, caseDescriptionUpdate)

                val caseDescriptionSubmit = "After clicking $ID_DETAIL_SUBMIT_BTN"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }

    @Test
    fun test03_checkTeacherStudentDetailSingleGradeEditSubmission() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[MICHAEL] as Student
        val studentIndex = students.indexOf(student)
        val gradeIndexChanged = 1
        val updatedStudent = student.copy(grades = student.grades.copy(
            grades = student.grades.grades.toMutableList().apply {
                this[gradeIndexChanged] = 100
            }
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate = "After editing $ID_GRADE_VALUE_ET at index $gradeIndexChanged"
                editGradesChangeAtIndex(gradeIndexChanged, updatedStudent, caseDescriptionUpdate)

                val caseDescriptionSubmit = "After clicking $ID_DETAIL_SUBMIT_BTN"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }

    @Test
    fun test04_checkTeacherStudentDetailExamGradeNegativeOutOfBounds() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[HARRISON] as Student
        val studentIndex = students.indexOf(student)
        val outOfBounds = student.copy(grades = student.grades.copy(
            exam = -100
        ))
        val updatedStudent = student.copy(grades = student.grades.copy(
            exam = -1
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate = "After editing $ID_STUDENT_EXAM_ET with negative out of bounds grade"
                editExamChange(outOfBounds, caseDescriptionUpdate)

                val caseDescriptionSubmit = "After clicking $ID_DETAIL_SUBMIT_BTN after editing with negative out of bounds grade"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }

    @Test
    fun test05_checkTeacherStudentDetailExamGradePositiveOutOfBounds() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[BENSON] as Student
        val studentIndex = students.indexOf(student)
        val outOfBoundsStudent = student.copy(grades = student.grades.copy(
            exam = 357
        ))
        val updatedStudent = student.copy(grades = student.grades.copy(
            exam = 100
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate = "After editing $ID_STUDENT_EXAM_ET with positive out of bounds grade"
                editExamChange(outOfBoundsStudent, caseDescriptionUpdate)

                val caseDescriptionSubmit = "After clicking $ID_DETAIL_SUBMIT_BTN after editing with positive out of bounds grade"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }

    @Test
    fun test06_checkTeacherStudentDetailSingleGradeEditNegativeOutOfBounds() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[MARTIN] as Student
        val studentIndex = students.indexOf(student)
        val gradeIndexChanged = 4
        val outOfBoundsStudent = student.copy(grades = student.grades.copy(
            grades = student.grades.grades.toMutableList().apply {
                this[gradeIndexChanged] = -100
            }
        ))
        val updatedStudent = student.copy(grades = student.grades.copy(
            grades = student.grades.grades.toMutableList().apply {
                this[gradeIndexChanged] = -1
            }
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate =
                    "After editing $ID_GRADE_VALUE_ET at index $gradeIndexChanged " +
                            "with negative out of bounds grade"
                editGradesChangeAtIndex(gradeIndexChanged, outOfBoundsStudent, caseDescriptionUpdate)

                val caseDescriptionSubmit =
                    "After clicking $ID_DETAIL_SUBMIT_BTN with negative out of bounds grade"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }

    @Test
    fun test07_checkTeacherStudentDetailSingleGradeEditPositiveOutOfBounds() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[LUCAS] as Student
        val studentIndex = students.indexOf(student)
        val gradeIndexChanged = 0
        val outOfBoundsStudent = student.copy(grades = student.grades.copy(
            grades = student.grades.grades.toMutableList().apply {
                this[gradeIndexChanged] = 500
            }
        ))
        val updatedStudent = student.copy(grades = student.grades.copy(
            grades = student.grades.grades.toMutableList().apply {
                this[gradeIndexChanged] = 100
            }
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate =
                    "After editing $ID_GRADE_VALUE_ET at index $gradeIndexChanged " +
                            "with positive out of bounds grade"
                editGradesChangeAtIndex(gradeIndexChanged, outOfBoundsStudent, caseDescriptionUpdate)

                val caseDescriptionSubmit =
                    "After clicking $ID_DETAIL_SUBMIT_BTN with positive out of bounds grade"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }

    @Test
    fun test08_checkTeacherStudentDetailExamGradeEmptyString() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[HARRISON] as Student
        val studentIndex = students.indexOf(student)
        val updatedStudent = student.copy(grades = student.grades.copy(
            exam = -1
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate = "After editing $ID_STUDENT_EXAM_ET with empty string grade"
                editExamChangeWithString("", updatedStudent, caseDescriptionUpdate)

                val caseDescriptionSubmit = "After clicking $ID_DETAIL_SUBMIT_BTN after editing exam grade with empty string"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }

    @Test
    fun test09_checkTeacherStudentDetailSingleGradeEditEmptyString() {
        val name = GEORGE
        val teacher = MockUserDatabase.users[name]!! as Teacher

        val students = MockUserDatabase.users
            .values
            .filterIsInstance<Student>()
        val student = MockUserDatabase.users[LUCAS] as Student
        val studentIndex = students.indexOf(student)
        val gradeIndexChanged = 4

        val updatedStudent = student.copy(grades = student.grades.copy(
            grades = student.grades.grades.toMutableList().apply {
                this[gradeIndexChanged] = -1
            }
        ))

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
                assertGetRequestWithToken(caseDescription, teacher.token, PATH_TEACHER_STUDENTS)
                clickStudent(studentIndex, caseDescription)
            }
            TeacherStudentScreen(this, TEACHER_STUDENT_SCREEN_NAME).apply {
                val caseDescriptionClickItem =
                    "After clicking on ${student.username} item in $TEACHER_SCREEN_NAME"
                assertGetRequestWithToken(
                    caseDescriptionClickItem,
                    teacher.token,
                    "$PATH_TEACHER_STUDENTS${student.username}/"
                )
                blackBoardMockBackEnd.clearResponseList()
                assertStudentDetails(student, caseDescriptionClickItem)

                val caseDescriptionUpdate =
                    "After editing $ID_GRADE_VALUE_ET at index $gradeIndexChanged " +
                            "with empty string grade"
                editGradesChangeAtIndexWithString(
                    "", gradeIndexChanged, updatedStudent, caseDescriptionUpdate
                )

                val caseDescriptionSubmit =
                    "After clicking $ID_DETAIL_SUBMIT_BTN with empty string grade at index $gradeIndexChanged"
                submitChangesAssertPatch(caseDescriptionSubmit, teacher, updatedStudent)
                assertStudentDetails(updatedStudent, caseDescriptionSubmit)
            }
        }
    }
}