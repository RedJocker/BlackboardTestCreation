package org.hyperskill.blackboard.util

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

    val intDiffcallback = object: DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int)
                = oldItem == newItem

        override fun areContentsTheSame(oldItem: Int, newItem: Int)
                = oldItem == newItem
    }

    val StudentDiffcallback = object: DiffUtil.ItemCallback<Student>(){
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }

    }
}