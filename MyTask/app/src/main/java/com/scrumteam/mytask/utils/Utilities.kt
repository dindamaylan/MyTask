package com.scrumteam.mytask.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.scrumteam.mytask.R
import java.time.DayOfWeek
import java.time.Month
import java.time.format.TextStyle
import java.util.*

enum class StatusSnackBar {
    SUCCESS,
    WARNING,
    DANGER
}

open class Event<out T>(private val content: T) {

    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

fun View.margin(
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.run { leftMargin = dpToPx(this) }
        top?.run { topMargin = dpToPx(this) }
        right?.run { rightMargin = dpToPx(this) }
        bottom?.run { bottomMargin = dpToPx(this) }
    }
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun mergeFullName(firstName: String, lastName: String): String {
    return firstName.plus(" ").plus(lastName)
}

fun splitFullName(fullName: String): List<String> {
    return fullName.split(" ")
}

fun String.isNotValidEmail() =
    this.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun validNewPassword(password: String, confirmPassword: String): Boolean {
    return password.equals(confirmPassword, ignoreCase = true)
}

fun Context.showSnackbar(
    view: View,
    content: String,
    status: StatusSnackBar,
    attachView: View? = null,
    action: (() -> Unit)? = null,
    actionText: String? = null
) {
    val snackbar = Snackbar.make(view, content, 5000)

    when (status) {
        StatusSnackBar.SUCCESS -> snackbar.setBackgroundTint(
            ContextCompat.getColor(
                this,
                R.color.success
            )
        )
        StatusSnackBar.WARNING -> snackbar.setBackgroundTint(
            ContextCompat.getColor(
                this,
                R.color.warning
            )
        )
        StatusSnackBar.DANGER -> snackbar.setBackgroundTint(
            ContextCompat.getColor(
                this,
                R.color.danger
            )
        )
    }

    snackbar.isGestureInsetBottomIgnored = true
    if (attachView != null) {
        snackbar.anchorView = attachView
    }

    if (action != null) {
        snackbar.setAction(actionText) {
            action()
        }
    }
    snackbar.show()
}

fun Fragment.showSnackbar(
    view: View,
    content: String,
    status: StatusSnackBar,
    attachView: View? = null,
    action: (() -> Unit)? = null,
    actionText: String? = null
) {
    requireContext().showSnackbar(
        view, content, status, attachView, action, actionText
    )
}

fun hideSoftKeyboard(context: Context, view: View) {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showSoftKeyboard(context: Context, view: View) {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun Context.getUriFromDrawable(@DrawableRes resId: Int): Uri {
    return Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(
            resId
        ) + '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(
            resId
        )
    )
}

fun Fragment.getUriFromDrawable(@DrawableRes resId: Int): Uri {
    return requireContext().getUriFromDrawable(resId)
}

fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale("id", "ID")).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}