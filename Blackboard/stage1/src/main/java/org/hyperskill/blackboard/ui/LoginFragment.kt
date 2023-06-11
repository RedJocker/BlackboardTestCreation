package org.hyperskill.blackboard.ui

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import org.hyperskill.blackboard.R
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentLoginBinding
import org.hyperskill.blackboard.util.Extensions.showToast


class LoginFragment : Fragment() {

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

        titleBinding.blackboardTitle.apply {
            error = "NetworkError"
            isFocusableInTouchMode = true
            isFocusable = true
        }


        loginBinding.apply {

            loginSubmitBtn.setOnClickListener {
                println("click loginSubmitBtn")
                titleBinding.blackboardTitle.requestFocus()
                val isOk = loginPassEt.text.isNotBlank() && loginPassEt.text.isNotBlank()
                context?.also{ context ->
                    if(isOk) {
                        context.showToast("login ok")
                    } else {
                        loginUsernameEt.error = "login failed"
                    }
                }
            }

            loginUsernameEt.addTextChangedListener(afterTextChanged = clearEtError)
            loginPassEt.addTextChangedListener(afterTextChanged = clearEtError)
        }

        println(loginBinding)
    }
}