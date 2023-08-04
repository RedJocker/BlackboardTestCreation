package org.hyperskill.blackboard.ui.teacher.details

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import okhttp3.Call
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Student
import org.hyperskill.blackboard.network.student.dto.GradesResponse
import org.hyperskill.blackboard.network.teacher.TeacherClient
import org.hyperskill.blackboard.util.Extensions.combineWith
import org.hyperskill.blackboard.util.Util
import java.io.IOException

class TeacherStudentDetailsViewModel(
        private val teacherClient: TeacherClient,
        private val handler: Handler,
        val onUpdateSuccess: () -> Unit
) : ViewModel() {

    private val _grades: MutableLiveData<List<Int>> = MutableLiveData(listOf())
    val grades: LiveData<List<Int>>
        get() = _grades

    private var _examGrade: MutableLiveData<Int> = MutableLiveData(-1)
    val examGrade: LiveData<Int>
        get() = _examGrade

    private val _editedGrades: MutableLiveData<List<Int>> = MutableLiveData(listOf())
    val editedGrades : LiveData<List<Int>>
        get() = _editedGrades

    private var _editedExamGrade: MutableLiveData<Int> = MutableLiveData(-1)
    val editedExamGrade: LiveData<Int>
        get() = _editedExamGrade.also { println("_editedExamGrade ${it.value}") }

    val partialGrade = editedGrades.map { grades ->
        (if(grades.isEmpty())
            0
        else
            grades.sumOf { if (it < 0) 0 else it } / grades.size)
        .also { println("partialGrade $it") }
    }

    val finalGrade = partialGrade.combineWith(editedExamGrade) { partial, exam ->
        when {
            partial == null -> null
            exam == null -> partial
            exam < 0 -> partial
            isExamEnabled.value == true -> (partial + exam) / 2
            else -> partial
        }.also { println("finalGrade $it") }
    }

    val isExamEnabled = partialGrade.map { partialGrade ->
        (partialGrade in (30 until 70)).also { println("isExamEnabled $it") }
    }


    val partialResult = partialGrade.map {partialGrade ->
        "Partial Result: $partialGrade"
    }.also { println("partialResult ${it.value}") }

    val finalResult = finalGrade.map { finalGrade ->
        val finalGradeString = if(finalGrade != null) "$finalGrade" else ""
        "Final Result: $finalGradeString"
    }

    private val _networkErrorMessage: MutableLiveData<String> = MutableLiveData("")
    val networkErrorMessage: LiveData<String>
        get() = _networkErrorMessage

    private var fetchGradesCall : Call? = null

    fun updateGrades(credential: Credential, studentName: String) {
        _grades.value = _editedGrades.value
        _examGrade.value = editedExamGrade.value

        val exam = if (isExamEnabled.value == true) editedExamGrade.value else examGrade.value
        teacherClient.updateGradesRequest(
            credential, studentName, editedGrades.value!!, exam ?: - 1, Util.callback(
                onFailure = ::onCallFailure,
                onResponse = ::onUpdateGradesResponse
            )
        )
    }

    fun fetchGrades(credential: Credential, student: Student) {
        fetchGradesCall = teacherClient.fetchStudentGrades(
            credential, student, Util.callback(
                onFailure = ::onCallFailure,
                onResponse = ::onFetchGradesResponse
            )
        )
    }

    private fun onCallFailure(call: Call, e: IOException) {
        println(call)
        handler.post {
            _networkErrorMessage.value = e.message
        }
    }

    private fun onUpdateGradesResponse(call: Call, response: Response) {
        println(call)
        handler.apply {
            println(response)
            when(response.code) {
                200 -> {
                    post {  }

                }
                else -> {
                    post {
                        _networkErrorMessage.value = "${response.code} ${response.message}"
                    }
                }
            }
        }
    }

    private fun onFetchGradesResponse(call: Call, response: Response) {
        println(call)
        handler.apply {
            println(response)
            when(response.code) {
                200 -> {
                    val gradesResponse = teacherClient.parseGrades(response.body)
                    if(gradesResponse == null || gradesResponse is GradesResponse.Fail) {
                        post {
                            _networkErrorMessage.value =
                                "server error, invalid response body ${response.body?.string()}"
                        }
                    } else {
                        post {
                            (gradesResponse as? GradesResponse.Success)?.also {

                                if(it.exam > 0) {
                                    it.grades.map { grade -> if(grade < 0) 0 else grade }
                                } else {
                                    it.grades
                                }.also { gradesList ->
                                    _grades.value = gradesList
                                    _editedGrades.value = gradesList
                                }

                                _examGrade.value = it.exam
                                _editedExamGrade.value = it.exam
                            }
                        }
                    }
                }
                else -> {
                    post {
                        _networkErrorMessage.value = "${response.code} ${response.message}"
                    }
                }
            }
        }
    }

    fun setEditedGrades(editedGrades: List<Int>) {
        _editedGrades.value = editedGrades
    }

    fun setEditedExamGrades(editedExamGrades: Int) {
        _editedExamGrade.value = editedExamGrades
    }

    class Factory(private val teacherClient: TeacherClient, private val handler: Handler, val onUpdateSuccess: () -> Unit) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TeacherStudentDetailsViewModel(teacherClient, handler, onUpdateSuccess) as T
        }
    }
}