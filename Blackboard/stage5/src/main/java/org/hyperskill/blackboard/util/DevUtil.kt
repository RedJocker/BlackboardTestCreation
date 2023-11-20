package org.hyperskill.blackboard.util

import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.ui.login.LoginFragment

object DevUtil {

    fun LoginFragment.loginTeacher() {
        onValidLogin(Credential(
            username =  "George",
            token =  "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiVEVBQ0hFUiIsInN1YiI6Ikdlb3JnZSIsImlzcyI6ImJsYWNrQm9hcmRBcHAifQ.hY4fC9rkQniZMmSIREK9esqUpxK187gkEgJl4pgt_iA",
            role =  Credential.Role.TEACHER
        ))
    }

    fun LoginFragment.loginLucas() {
        onValidLogin(Credential(
                username =  "Lucas",
                token =  "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiU1RVREVOVCIsInN1YiI6Ikx1Y2FzIiwiaXNzIjoiYmxhY2tCb2FyZEFwcCJ9.pVczvoquyVDNWqXeVJB6cwlv0I2VRzu0AmAwqwKosnY",
                role =  Credential.Role.STUDENT
        ))
    }
}