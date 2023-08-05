package org.hyperskill.blackboard.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HorizontalLinearLayoutManager(
        context: Context
) : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
    override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect, immediate: Boolean, focusedChildVisible: Boolean): Boolean {
        return if ((child as ViewGroup).focusedChild is EditText) {
            false
        } else super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible)
    }
}