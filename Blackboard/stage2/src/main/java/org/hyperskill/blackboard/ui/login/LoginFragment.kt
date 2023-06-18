package org.hyperskill.blackboard.ui.login

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentLoginBinding
import org.hyperskill.blackboard.util.Extensions.showToast


class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        LoginViewModel.Factory(application.loginClient, Handler(activity.mainLooper))
    }

    lateinit var loginBinding: FragmentLoginBinding
    lateinit var titleBinding: BlackboardTitleBinding

    private val clearEtError: (text: Editable?) -> Unit = {
        println("clearEtError")
        loginBinding.loginUsernameEt.error = null
        titleBinding.blackboardTitle.error = null
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("LoginFragment.onCreateView")
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        titleBinding = BlackboardTitleBinding.bind(loginBinding.root)
        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("LoginFragment.onViewCreated")

        loginBinding.apply {

            loginSubmitBtn.setOnClickListener {
                println("click loginSubmitBtn")

                val username = loginUsernameEt.text.toString()
                val plainPass = loginPassEt.text.toString()

                loginViewModel.makeLogin(username, plainPass)
            }

            loginViewModel.credential.observe(viewLifecycleOwner) { credential ->
                println("credential.observe: $credential")
                if(credential != null) {
                    onValidLogin(credential)
                }
            }

            loginViewModel.messageLoginError.observe(viewLifecycleOwner) { errorMessage ->
                println("messageLoginError.observe: $errorMessage")
                if(errorMessage != null) {
                    onInvalidLogin(errorMessage)
                } else {
                    loginBinding.loginUsernameEt.error = null
                }
            }

            loginViewModel.messageNetworkError.observe(viewLifecycleOwner) { errorMessage ->
                println("messageNetworkError.observe: $errorMessage")
                if (errorMessage != null) {
                    context!!.showToast(errorMessage)
                } else {

                }
            }

            loginUsernameEt.addTextChangedListener(afterTextChanged = clearEtError)
            loginPassEt.addTextChangedListener(afterTextChanged = clearEtError)
        }

        println(loginBinding)
    }

    private fun onValidLogin(credential: Credential) {
        val message = when (credential.role) {
            Credential.Role.STUDENT -> "Hello ${credential.username}"
            Credential.Role.TEACHER -> "Good day teacher ${credential.username}"
        }
        context!!.showToast(message)
        clearUserInput()
    }

    private fun clearUserInput() {
        loginBinding.loginPassEt.setText("")
        loginBinding.loginUsernameEt.setText("")
    }

    private fun onInvalidLogin(message: String) {
        loginBinding.loginPassEt.setText("")
        loginBinding.loginUsernameEt.error = message
        loginBinding.loginUsernameEt.requestFocus()
    }
}