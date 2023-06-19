package org.hyperskill.blackboard.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hyperskill.blackboard.data.model.Credential

class StudentViewModel(
    private val credential: Credential,
    /*private val studentClient: StudentClient, */
    /*private val handler: Handler*/) : ViewModel()
{
    val username
        get() = credential.username


    class Factory(private val credential: Credential): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(Credential::class.java)
                .newInstance(credential)
        }
    }
}