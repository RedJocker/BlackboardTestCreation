package org.hyperskill.blackboard.ui.student

import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import okhttp3.Call
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.student.StudentClient
import org.hyperskill.blackboard.network.student.dto.GradesResponse
import org.hyperskill.blackboard.util.Unique
import org.hyperskill.blackboard.util.Util.callback
import java.io.IOException

class StudentViewModel(
        private val credential: Credential,
        private val studentClient: StudentClient,
        private val handler: Handler
) : ViewModel() {

    val username get() = credential.username

    private val _grades: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())
    val grades: StateFlow<List<Int>> get() = _grades

    private val _predictionGrades: MutableStateFlow<Unique<List<Int>>> = MutableStateFlow(Unique(listOf()))
    val predictionGrades: Flow<List<Int>> get() = _predictionGrades.map { it.value }

    private var _examGrade: MutableStateFlow<Int> = MutableStateFlow(-1)
    val examGrade: StateFlow<Int> get() = _examGrade

    private var _predictionExamGrade: MutableStateFlow<Int> = MutableStateFlow(-1)
    val predictionExamGrade: StateFlow<Int> get() = _predictionExamGrade

    val partialGrade = grades.map { grades ->
        if(grades.isEmpty())
            0
        else
            grades.sumOf { if (it < 0) 0 else it } / grades.size
    }

    val partialPredictionGrade = predictionGrades.map { grades ->
        if(grades.isEmpty())
            0
        else
            grades.sumOf { if (it < 0) 0 else it } / grades.size
    }

    val partialGradeText = partialGrade.combine(partialPredictionGrade)
    { partialGrade, partialPredictionGrade ->
        println("partialGrade.combine(partialPredictionGrade)")
        if (partialGrade != partialPredictionGrade)
            "Partial Result: $partialGrade ($partialPredictionGrade)"
        else
            "Partial Result: $partialGrade"
    }

    private val hasRemainingTests = grades.map { grades ->
        grades.any { it < 0 }
    }

    private val hasRemainingTestsPrediction = predictionGrades.map { grades ->
        grades.any { it < 0 }
    }

    val finalGrade = hasRemainingTests.combine(examGrade) { hasRemainingTests, examGrade ->
        hasRemainingTests to examGrade
    }.combine(partialGrade) { (hasRemainingTests, exam), partialGrade ->
        val isExamPossible = partialGrade in 30 until 70
        println("hasRemainingTests: $hasRemainingTests, isExamPossible: $isExamPossible, exam: $exam")
        if (exam >= 0) {
            if (isExamPossible) {
                (partialGrade + exam) / 2
            } else {
                partialGrade
            }
        } else {
            if(hasRemainingTests || isExamPossible) {
                -1
            } else {
                partialGrade
            }
        }
    }

    val finalGradeText = finalGrade.map { finalGrade ->
        val finalText = if (finalGrade < 0)
            ""
        else
            "$finalGrade"
        "Final Result: $finalText"
    }

    private val _messageNetworkError = MutableStateFlow<String?>(null)
    val messageNetworkError: StateFlow<String?> get() = _messageNetworkError


    private var fetchGradesCall : Call? = null
    private val gradesCallback = callback(
            onFailure = ::onFetchGradesFailure,
            onResponse = ::onFetchGradesResponse
    )


    fun fetchGrades() {
        fetchGradesCall = studentClient.fetchGrades(credential, gradesCallback)
    }

    private fun onFetchGradesFailure(call: Call, e: IOException) {
        println(call)
        handler.post {
            _messageNetworkError.value = e.message
        }
    }

    private fun onFetchGradesResponse(call: Call, response: Response) {
        println(call)
        handler.apply {
            println(response)
            when(response.code) {
                200 -> {
                    val gradesResponse = studentClient.parseGrades(response.body)
                    if(gradesResponse == null || gradesResponse is GradesResponse.Fail) {
                        post {
                            _messageNetworkError.value =
                                    "server error, invalid response body ${response.body?.string()}"
                        }
                    } else {
                        post {
                            (gradesResponse as? GradesResponse.Success)?.also {

                                val normGrades = if (it.exam > 0) {
                                    it.grades.map { grade -> if (grade < 0) 0 else grade }
                                } else {
                                    it.grades
                                }
                                _grades.value = normGrades
                                _predictionGrades.value = Unique(normGrades)
                                _examGrade.value = it.exam
                                _predictionExamGrade.value = it.exam
                                _messageNetworkError.value = null
                            }
                        }
                    }
                }
                else -> {
                    post {
                        _messageNetworkError.value = "${response.code} ${response.message}"
                    }
                }
            }
        }
    }

    fun setPredictionGradesList(predictionGrades: List<Int>) {
        println("setPredictionGradesList $predictionGrades ${_predictionGrades.value == predictionGrades}")
        _predictionGrades.value = Unique(predictionGrades)
    }

    fun setPredictionExamGrade(predictionExamGrade: Int) {
        println("setPredictionExamGrade $predictionExamGrade")
        _predictionExamGrade.value = predictionExamGrade
    }


    class Factory(
            private val credential: Credential,
            private val studentClient: StudentClient,
            private val handler: Handler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StudentViewModel(credential, studentClient, handler) as T
        }
    }
}