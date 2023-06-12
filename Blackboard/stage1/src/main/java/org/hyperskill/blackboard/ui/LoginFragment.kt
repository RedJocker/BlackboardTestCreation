package org.hyperskill.blackboard.ui

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentLoginBinding
import org.hyperskill.blackboard.util.Extensions.showToast
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


class LoginFragment : Fragment() {

    lateinit var loginBinding: FragmentLoginBinding
    lateinit var titleBinding: BlackboardTitleBinding

    val defaultUserMap = mapOf(
            "George" to ("TEACHER" to "A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ="), // plainPass = 1234
            "Lucas" to ("STUDENT" to "SfjRCkJKMPlfohEJae0FOjNlJYbaGQ++tcY3LWnX40Q="),  // plainPass = 32A1
    )

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    val userMap by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().intent
                .getSerializableExtra("userMap") as? Map<String, Pair<String, String>>
                ?: defaultUserMap
    }

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

                if(userMap.containsKey(username)) {
                    val (role, storedPass) = userMap[username]!!
                    if(encryptPass(plainPass) == storedPass) {
                        onValidLogin(username, role)
                    } else {
                        onInvalidLogin()
                    }
                } else {
                    onInvalidLogin()
                }

            }

            loginUsernameEt.addTextChangedListener(afterTextChanged = clearEtError)
            loginPassEt.addTextChangedListener(afterTextChanged = clearEtError)
        }

        println(loginBinding)
    }

    private fun onValidLogin(username: String, role: String) {
        val message = when (role) {
            "STUDENT" -> "Hello $username"
            "TEACHER" -> "Good day teacher $username"
            else -> throw IllegalArgumentException("Invalid Role $role")
        }
        context!!.showToast(message)
        clearUserInput()
    }

    private fun clearUserInput() {
        loginBinding.loginPassEt.setText("")
        loginBinding.loginUsernameEt.setText("")
    }

    private fun onInvalidLogin() {
        loginBinding.loginPassEt.setText("")
        loginBinding.loginUsernameEt.error = "Invalid Login"
        loginBinding.loginUsernameEt.requestFocus()
    }

    fun encryptPass(pass: String): String {
        val rawPassBytes = pass.toByteArray(StandardCharsets.UTF_8)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val sha256HashPass = messageDigest.digest(rawPassBytes)
        val base64sha256HashPass = android.util.Base64.encodeToString(
                sha256HashPass, android.util.Base64.NO_WRAP
        )
        return base64sha256HashPass
    }
}