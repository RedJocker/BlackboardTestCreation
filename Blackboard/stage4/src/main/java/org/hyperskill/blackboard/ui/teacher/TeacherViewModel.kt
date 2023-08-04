package org.hyperskill.blackboard.ui.teacher

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import okhttp3.Call
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Student
import org.hyperskill.blackboard.network.teacher.TeacherClient
import org.hyperskill.blackboard.network.teacher.dto.StudentsResponse
import org.hyperskill.blackboard.util.Util
import java.io.IOException

class TeacherViewModel(
        private val teacherClient: TeacherClient,
        private val handler: Handler
) : ViewModel() {

    private val _students = MutableLiveData(listOf<Student>())
    val students : LiveData<List<Student>>
        get() = _students

    private val _networkErrorMessage: MutableLiveData<String> = MutableLiveData("")
    val networkErrorMessage: LiveData<String>
        get() = _networkErrorMessage

    private var fetchGradesCall : Call? = null

    fun fetchStudents(credential: Credential) {
        fetchGradesCall =  teacherClient.students(credential, Util.callback(
            onFailure = ::onFetchStudentsFailure,
            onResponse = ::onFetchStudentsResponse
        ))
    }



    private fun onFetchStudentsFailure(call: Call, e: IOException) {
        println(call)
        handler.post {
            _networkErrorMessage.value = e.message
        }
    }

    private fun onFetchStudentsResponse(call: Call, response: Response) {
        println(call)
        handler.apply {
            println(response)
            when(response.code) {
                200 -> {
                    val gradesResponse = teacherClient.parseStudents(response.body)
                    if(gradesResponse == null || gradesResponse is StudentsResponse.Fail) {
                        post {
                            _networkErrorMessage.value =
                                "server error, invalid response body ${response.body?.string()}"
                        }
                    } else {
                        post {
                            (gradesResponse as? StudentsResponse.Success)?.also {
                                _students.value = it.students
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

    class Factory(private val teacherClient: TeacherClient, private val handler: Handler): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(TeacherClient::class.java, Handler::class.java)
                .newInstance(teacherClient, handler)
        }
    }
}