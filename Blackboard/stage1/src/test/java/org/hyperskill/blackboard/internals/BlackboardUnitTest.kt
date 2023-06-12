package org.hyperskill.blackboard.internals

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

open class BlackboardUnitTest<T : Activity>(clazz: Class<T>): AbstractUnitTest<T>(clazz) {

    internal fun TextView.assertText(
            expectedText: String,
            idString: String,
            caseDescription: String) {

        val actualText = text.toString()
        val message = "$caseDescription, on property text of TextView with id $idString"

        assertEquals(message, expectedText, actualText)
    }

    internal fun Button.assertText(
            expectedText: String,
            idString: String,
            caseDescription: String) {

        val actualText = text.toString()
        val message = "$caseDescription, on property text of Button with id $idString"

        assertEquals(message, expectedText, actualText)
    }

    internal fun EditText.assertText(
            expectedText: String,
            idString: String,
            caseDescription: String) {

        val actualText = text.toString()
        val message = "$caseDescription, on property text of EditText with id $idString"
        assertEquals(message, expectedText, actualText)
    }

    internal fun EditText.assertError(
            expectedError: String?,
            idString: String,
            caseDescription: String) {

        val actualError = error?.toString()
        val message = "$caseDescription, on property error of EditText with id $idString"
        assertEquals(message, expectedError, actualError)
    }

    internal fun EditText.assertFocus(
            expectedIsFocused: Boolean,
            idString: String,
            caseDescription: String) {

        val actualIsFocused = isFocused
        val message = "$caseDescription, on property isFocused of EditText with id $idString"
        assertEquals(message, expectedIsFocused, actualIsFocused)
    }

    internal fun EditText.assertHint(
            expectedHint: String,
            idString: String,
            caseDescription: String) {

        val actualHint = hint.toString()
        val message = "$caseDescription, on property hint of EditText with id $idString"
        assertEquals(message, expectedHint, actualHint)
    }

    internal fun EditText.assertInputType(
            expectedInputType: Int,
            inputTypeString: String,
            idString: String,
            caseDescription: String) {

        val actualInputType = inputType

        val message = "$caseDescription, on property inputType of EditText with id $idString " +
                "expected $inputTypeString($expectedInputType), but was _($actualInputType)"
        assertTrue(message, expectedInputType == actualInputType)
    }

    internal fun EditText.assertValues(
            expectedText: String,
            expectedHint: String,
            expectedInputType: Int,
            inputTypeString: String,
            idString: String,
            caseDescription: String) {
        assertText(
                expectedText = expectedText,
                idString = idString,
                caseDescription = caseDescription)
        assertHint(
                expectedHint = expectedHint,
                idString = idString,
                caseDescription = caseDescription)
        assertInputType(
                expectedInputType = expectedInputType,
                inputTypeString = inputTypeString,
                idString = idString,
                caseDescription = caseDescription)
    }
}