package org.hyperskill.blackboard.util

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.hyperskill.blackboard.data.model.Student
import java.io.IOException

object Util {
    fun callback(onFailure: (call: Call, e: IOException)  -> Unit,
                 onResponse: (call: Call, response: Response) -> Unit )
            : Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) = onFailure(call, e)
            override fun onResponse(call: Call, response: Response) =  onResponse(call, response)
        }
    }

    val uniqueIntCallback = object: DiffUtil.ItemCallback<Unique<Int>>() {
        override fun areItemsTheSame(oldItem: Unique<Int>, newItem: Unique<Int>): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Unique<Int>, newItem: Unique<Int>): Boolean {
            return false
        }
    }

    val intDiffcallback = object: DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int)
                = false

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            println("oldItem: $oldItem, newItem: $newItem")
            return if (newItem == -1 ) false else oldItem == newItem
        }

    }

    val studentDiffCallback = object: DiffUtil.ItemCallback<Student>(){
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }

    }

    val onFocusEditTextPutCursorEnd = View.OnFocusChangeListener { v, hasFocus ->
        println("v $v hasFocus $hasFocus")
        if (v !is EditText) return@OnFocusChangeListener
        if(hasFocus) {
            v.setSelection(v.text.length)
        }
    }
}
