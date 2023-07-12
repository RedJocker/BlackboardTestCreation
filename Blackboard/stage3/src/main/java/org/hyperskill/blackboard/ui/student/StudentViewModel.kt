package org.hyperskill.blackboard.ui.student

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import okhttp3.Call
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.network.student.StudentClient
import org.hyperskill.blackboard.network.student.dto.GradesResponse
import org.hyperskill.blackboard.util.Extensions.combineWith
import org.hyperskill.blackboard.util.Util.callback
import java.io.IOException

class StudentViewModel(
        private val credential: Credential,
        private val studentClient: StudentClient,
        private val handler: Handler
) : ViewModel() {

    val username get() = credential.username

    private val _grades: MutableLiveData<List<Int>> = MutableLiveData(listOf())
    val grades: LiveData<List<Int>> get() = _grades

    private var _examGrade: MutableLiveData<Int> = MutableLiveData(-1)
    val examGrade: LiveData<Int> get() = _examGrade

    val partialGrade = grades.map { grades ->
        if(grades.isEmpty())
            0
        else
            grades.sumOf { if (it < 0) 0 else it } / grades.size
    }.also { println("partialGrade ${it.value}") }

    val finalGrade = partialGrade.combineWith(examGrade) { partial, exam ->
        when {
            partial == null -> null
            exam == null -> partial
            exam < 0 -> partial
            else -> (partial + exam) / 2
        }.also { println("finalGrade $it") }
    }

    private val _messageNetworkError = MutableLiveData<String?>(null)
    val messageNetworkError: LiveData<String?> get() = _messageNetworkError


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


                                _grades.value = if(it.exam > 0) {
                                    it.grades.map { grade -> if(grade < 0) 0 else grade }
                                } else {
                                    it.grades
                                }
                                _examGrade.value = it.exam
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