package org.hyperskill.blackboard

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.hyperskill.blackboard.network.login.LoginClient
import org.hyperskill.blackboard.network.student.StudentClient
import org.hyperskill.blackboard.network.teacher.TeacherClient

class BlackboardApplication: Application() {

    val loginClient by lazy {
        LoginClient(okHttpClient, moshi)
    }

    val studentClient by lazy {
        StudentClient(okHttpClient, moshi)
    }

    val teacherClient by lazy {
        TeacherClient(okHttpClient, moshi)
    }

    private val okHttpClient by lazy {
        OkHttpClient()
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}