package org.hyperskill.blackboard.internals

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowActivity
import org.robolectric.shadows.ShadowAlertDialog
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowToast
import java.time.Duration

abstract class AbstractUnitTest<T : Activity>(clazz: Class<T>) {

    /**
     * Setup and control activities and their lifecycle
     */
    val activityController: ActivityController<T> by lazy {
        Robolectric.buildActivity(clazz)
    }

    /**
     * The activity being tested.
     *
     * It is the @RealObject of the shadowActivity
     */
    val activity : Activity by lazy {
        activityController.get()
    }

    /**
     * A Roboletric shadow object of the Activity class, contains helper methods to deal with
     * testing activities like setting permissions, peeking results of launched activities for result,
     * retrieving shown dialogs, intents and others.
     *
     * If you don't know what shadows are you can have a better understanding on that reading this
     * on roboletric documentation: http://robolectric.org/extending/
     *
     * Understanding Shadows is fundamental for Roboletric, things are not what they appear to be on
     * Roboletric because running a code on the jvm is not the same as running the code on a real/emulated device.
     * Code that expects to eventually talk to the machine won't have the machine they expect to have to talk to.
     * Shadow is how Roboletric makes things possible, they impersonate @RealObject and act when @RealObject is expected to act.
     *
     * Things in Roboletric are not what they appear to be.
     * It is possible to not notice it for the most part, but it will be essential for some other parts
     */
    val shadowActivity: ShadowActivity by lazy {
        Shadow.extract(activity)
    }

    /**
     * A Roboletric shadow object of the mainLooper. Handles enqueued runnables and also the passage of time.
     *
     * Usually used with .idleFor(someDurationValue) or .runToEndOfTasks()
     */
    val shadowLooper: ShadowLooper by lazy {
        shadowOf(activity.mainLooper)
    }

    /**
     * Setup an activity for a robolectric test.
     *
     * It accepts an Intent as argument that will be the intent associated with the activity.
     * It also accepts a Bundle which that will be the savedInstanceState associated with the activity
     *
     * The Intent may be used to pass input from tests to the implementation side through the extras.
     *
     * Returns a value for convenience use, like in tests that involve navigation between Activities.
     *
     * Captures exception to ensure better error messages displayed when tests are run with
     * check button and exceptions are thrown by user implementation
     */
    fun <ReturnValue> testActivity(arguments: Intent = Intent(), savedInstanceState: Bundle = Bundle(), testCodeBlock: (Activity) -> ReturnValue): ReturnValue {
        try {
            activity.intent =  arguments
            activityController.setup(savedInstanceState)
        } catch (ex: Exception) {
            throw AssertionError("Exception, test failed on activity creation with $ex\n${ex.stackTraceToString()}")
        }

        return try {
            testCodeBlock(activity)
        } catch (ex: Exception) {
            throw AssertionError("Exception. Test failed on activity execution with $ex\n${ex.stackTraceToString()}")
        }
    }

    /**
     * Use this method to find views.
     *
     * The view existence will be assert before being returned
     */
    inline fun <reified T> Activity.findViewByString(idString: String): T {
        val id = this.resources.getIdentifier(idString, "id", this.packageName)
        val view: View? = this.findViewById(id)

        val idNotFoundMessage = "View with id \"$idString\" was not found"
        val wrongClassMessage = "View with id \"$idString\" is not from expected class. " +
                "Expected ${T::class.java.simpleName} found ${view?.javaClass?.simpleName}"

        assertNotNull(idNotFoundMessage, view)
        assertTrue(wrongClassMessage, view is T)

        return view as T
    }

    /**
     * Use this method to find views.
     *
     * The view existence will be assert before being returned
     */
    inline fun <reified T> View.findViewByString(idString: String): T {
        val id = this.resources.getIdentifier(idString, "id", context.packageName)
        val view: View? = this.findViewById(id)

        val idNotFoundMessage = "View with id \"$idString\" was not found"
        val wrongClassMessage = "View with id \"$idString\" is not from expected class. " +
                "Expected ${T::class.java.simpleName} found ${view?.javaClass?.simpleName}"

        assertNotNull(idNotFoundMessage, view)
        assertTrue(wrongClassMessage, view is T)

        return view as T
    }

    /**
     * Use this method to perform clicks. It will also advance the clock millis milliseconds and run
     * enqueued Runnable scheduled to run on main looper in that timeframe.
     * Default value for millis is 500
     *
     * Internally it calls performClick() and shadowLooper.idleFor(millis)
     */
    fun View.clickAndRun(millis: Long = 500){
        this.performClick()
        shadowLooper.idleFor(Duration.ofMillis(millis))
    }

    /**
     * Asserts that the last message toasted is the expectedMessage.
     * Assertion fails if no toast is shown with null actualLastMessage value.
     */
    fun assertLastToastMessageEquals(errorMessage: String, expectedMessage: String,) {
        val actualLastMessage: String? = ShadowToast.getTextOfLatestToast()
        assertEquals(errorMessage, expectedMessage, actualLastMessage)
    }

    /**
     * Use this method to retrieve the latest AlertDialog.
     *
     * The existence of such AlertDialog will be asserted before returning.
     *
     * Robolectric only supports android.app.AlertDialog, test will not be
     * able to find androidx.appcompat.app.AlertDialog.
     *
     * - Important!!! :
     * When writing stage description state explicitly the correct version that should be imported
     */
    fun getLatestDialog(): AlertDialog {
        val latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog()

        assertNotNull(
            "There was no AlertDialog found. Make sure to import android.app.AlertDialog version",
            latestAlertDialog
        )

        return latestAlertDialog!!
    }

    fun assertInternetPermission() {
        val packageManager = RuntimeEnvironment.getApplication().packageManager

        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                activity.packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(activity.packageName, PackageManager.GET_PERMISSIONS)
        }

        val permissions = packageInfo.requestedPermissions ?: emptyArray()
        val hasInternetPermission = permissions.contains(Manifest.permission.INTERNET)

        val messageNoInternetPermission =
            "Internet permission is not set in the AndroidManifest.xml file"
        assertTrue(messageNoInternetPermission, hasInternetPermission)
    }

    /**
     *  Makes assertions on the contents of the RecyclerView.
     *
     *  Asserts that the size matches the size of fakeResultList and then
     *  calls assertItems for each item of the list with the itemViewSupplier
     *  so that it is possible to make assertions on that itemView.
     *
     *  Take attention to refresh references to views coming from itemView since RecyclerView
     *  can change the instance of View for a determinate list item after an update of the list
     *  (ex: calling notifyItemChanged and similar methods).
     */

    fun <T> RecyclerView.assertListItems(
            fakeResultList: List<T>,
            assertItems: (itemViewSupplier: () -> View, position: Int, item: T) -> Unit
    ) : Unit {

        assertNotNull("Your recycler view adapter should not be null", this.adapter)

        val expectedSize = fakeResultList.size

        val actualSize = this.adapter!!.itemCount
        assertEquals("Incorrect number of list items", expectedSize, actualSize)

        if(expectedSize == 0) {
            return
        } else if(expectedSize > 0) {
            val firstItemViewHolder = (0 until expectedSize)
                    .asSequence()
                    .mapNotNull {  this.findViewHolderForAdapterPosition(it) }
                    .firstOrNull()
                    ?: throw AssertionError("No item is being displayed on RecyclerView, is it big enough to display one item?")

            val listWidth = firstItemViewHolder.itemView.width * (expectedSize + 1)

            for((i, song) in fakeResultList.withIndex()) {
                // setting width to ensure that all items are inflated. Height might change after assertItems, keep statement inside loop.
                this.layout(0,0, listWidth, this.height)  // may increase clock time

                val itemViewSupplier = {
                    scrollToPosition(i)
                    findViewHolderForAdapterPosition(i)?.itemView
                            ?: throw AssertionError("Could not find list item with index $i")
                }
                assertItems(itemViewSupplier, i, song)
            }

        } else {
            throw IllegalStateException("size assertion was not effective")
        }
    }

    /**
     *  Makes assertions on the contents of one item of the RecyclerView.
     *
     *  Asserts that the the size of the list is at least itemIndex + 1.
     *
     *  Calls assertItem with the itemViewSupplier so that it is possible to make assertions on that itemView.
     *  Take attention to refresh references to views coming from itemView since RecyclerView
     *  can change the instance of View for a determinate list item after an update to the list.
     */
    fun RecyclerView.assertSingleListItem(itemIndex: Int, assertItem: (itemViewSupplier: () -> View) -> Unit) {

        assertNotNull("Your recycler view adapter should not be null", this.adapter)

        val expectedMinSize = itemIndex + 1

        val actualSize = this.adapter!!.itemCount
        assertTrue(
                "RecyclerView was expected to contain item with index $itemIndex, but its size was $actualSize",
                actualSize >= expectedMinSize
        )

        if(actualSize >= expectedMinSize) {
            val firstItemViewHolder = (0 until actualSize)
                    .asSequence()
                    .mapNotNull {  this.findViewHolderForAdapterPosition(it) }
                    .firstOrNull()
                    ?: throw AssertionError("No item is being displayed on RecyclerView, is it big enough to display one item?")

            val listWidth = firstItemViewHolder.itemView.width * (expectedMinSize + 1)
            this.layout(0,0, listWidth, this.height)  // may increase clock time

            val itemViewSupplier = {
                this.scrollToPosition(itemIndex)
                val itemView = (this.findViewHolderForAdapterPosition(itemIndex)?.itemView
                        ?: throw AssertionError("Could not find list item with index $itemIndex"))
                itemView
            }

            assertItem(itemViewSupplier)

        } else {
            throw IllegalStateException("size assertion was not effective")
        }
    }
}
