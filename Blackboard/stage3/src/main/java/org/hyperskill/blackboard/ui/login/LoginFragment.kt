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
import androidx.navigation.fragment.findNavController
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.R
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Credential.Companion.putCredential
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentLoginBinding
import org.hyperskill.blackboard.network.BaseClient
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
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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

                if(BaseClient.baseurl.substring(0,15) != "http://10.0.2.2") titleBinding.blackboardTitle.error =
                    "invalid: nodename nor servname provided, or not known"


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
                    onNetworkError(errorMessage)
                } else {
                    titleBinding.blackboardTitle.error = null
                }
            }

            loginUsernameEt.addTextChangedListener(afterTextChanged = clearEtError)
            loginPassEt.addTextChangedListener(afterTextChanged = clearEtError)
        }
    }

    private fun onNetworkError(errorMessage: String) {
        println("LoginFragment.onNetworkError $errorMessage")
        titleBinding.blackboardTitle.error = errorMessage
        titleBinding.blackboardTitle.requestFocus()
    }

    private fun onValidLogin(credential: Credential) {
        println("LoginFragment.onValidLogin $credential")
        when (credential.role) {
            Credential.Role.STUDENT -> {
                val message = "Hello ${credential.username}"
                context!!.showToast(message)
                val args = Bundle().apply {
                    putCredential(credential)
                }
                findNavController().navigate(R.id.action_loginFragment_to_studentFragment, args)
                loginViewModel.clearCredential()
            }
            Credential.Role.TEACHER -> {
                val message = "Good day teacher ${credential.username}"
                context!!.showToast(message)
            }
        }

        clearUserInput()
    }

    private fun clearUserInput() {
        println("LoginFragment.clearUserInput")
        loginBinding.loginPassEt.setText("")
        loginBinding.loginUsernameEt.setText("")
    }

    private fun onInvalidLogin(message: String) {
        println("LoginFragment.onInvalidLogin")
        loginBinding.loginPassEt.setText("")
        loginBinding.loginUsernameEt.error = message
        loginBinding.loginUsernameEt.requestFocus()
    }
}