package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import android.widget.TextView
import org.hyperskill.blackboard.internals.BlackboardUnitTest

class BlackboardTitle<T: Activity>(
        private val screenName: String, private val test: BlackboardUnitTest<T>) {

    companion object {
        const val BLACKBOARD_TITLE_ID = "blackboard_title"
    }

    @Suppress("UNUSED")
    val blackboardTitle: TextView by lazy {
        with(test) {
            try {
                activity.findViewByString<TextView>(BLACKBOARD_TITLE_ID)
            } catch (e: AssertionError) {
                val message = "View with id \"$BLACKBOARD_TITLE_ID\" was not found on $screenName"
                throw AssertionError(message)
            }.apply {
                assertText(
                    expectedText = "::B L A C K B O A R D::",
                    idString = BLACKBOARD_TITLE_ID,
                    caseDescription = "On $screenName initialization"
                )
            }
        }
    }
}