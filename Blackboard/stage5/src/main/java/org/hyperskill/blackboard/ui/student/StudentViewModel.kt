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

    val predictionPartialGrade = predictionGrades.map { grades ->
        if(grades.isEmpty())
            0
        else
            grades.sumOf { if (it < 0) 0 else it } / grades.size
    }

    val partialGradeText = partialGrade.combine(predictionPartialGrade)
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

    val finalGradeData = examGrade.combine(partialGrade) { examGrade, partialGrade ->
        val isExamPossible = partialGrade in 30 until 70
        println("isExamPossible: $isExamPossible, examGrade: $examGrade")
        val finalGradeValue = if (isExamPossible) {
            (partialGrade + examGrade) / 2
        } else {
            partialGrade
        }
        Triple(finalGradeValue, isExamPossible, examGrade)
    }

    val finalGradeNormToFinalGradeValue = hasRemainingTests.combine(finalGradeData)
    { hasRemainingTests, (finaGradeValue, isExamPossible, examGrade) ->
        println("hasRemainingTests: $hasRemainingTests isExamPossible: $isExamPossible")
        if (examGrade >= 0) {
            finaGradeValue
        } else {
            if(hasRemainingTests || isExamPossible) {
                -1
            } else {
                finaGradeValue
            }
        } to finaGradeValue
    }

    val predictionFinalGrade = predictionExamGrade.combine(predictionPartialGrade)
    { predictionExamGrade, predictionPartialGrade ->
        val isExamPossible = predictionPartialGrade in 30 until 70
        val predictionExamGradeNorm = if (predictionExamGrade < 0) 0
            else if (predictionExamGrade > 100) 100
            else predictionExamGrade
        println("predictionIsExamPossible: $isExamPossible, predictionExamGradeNorm: $predictionExamGradeNorm")
        if (isExamPossible) {
            (predictionPartialGrade + predictionExamGradeNorm) / 2
        } else {
            predictionPartialGrade
        }
    }

    val finalGradeText = finalGradeNormToFinalGradeValue.combine(predictionFinalGrade)
    { (finalGradeNorm, finalGradeValue), predictionFinalGrade ->
        val predictionText =
            if (finalGradeValue == predictionFinalGrade) "" else " ($predictionFinalGrade)"
        val finalText = if (finalGradeNorm < 0) "" else "$finalGradeNorm"
        "Final Result: $finalText$predictionText"
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